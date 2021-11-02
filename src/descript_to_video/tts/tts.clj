(ns descript-to-video.tts.tts
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [descript-to-video.util.date :as d]
            [descript-to-video.util.file :as f]
            [descript-to-video.aviutl.aviutl :as aviutl]
            [descript-to-video.aviutl.parser :as parser]
            [descript-to-video.grpc.client :as g]))

;; 定数群
;; TODO:設定ファイルから取得
(def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\SpeechSample.exe")
(def separator #"-----\r\n")
(def default-output-path "output/voices/")


(defn get-library-list
  "SpeechSample.exeを実行し、結果から使用可能なTTSライブラリの一覧を取得する"
  []
  (let [rawOutput (shell/sh "cmd" "/c" ttsControllerPath "-v" :out-enc "shift-jis")
        exitCode (:exit rawOutput)
        [_ output _] (s/split (:out rawOutput) separator)]
    (cond
      (= exitCode 0) (s/split-lines output)
      :else [])))

(defn talk
  "TTSライブラリ名とテキストを指定し、SpeechSample.exeで喋らせる"
  [library text]
  (g/talk library text))

(defn gen-file-name
  [library text]
  (str (d/get-time-stamp) "_" library "_" text))

(defn save-to-file
  [library text]
  (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
        filepath (f/getAbsolutePath (str default-output-path (gen-file-name library joinedText)))
        wavFilePath (str filepath ".wav")
        textFileName (str filepath ".txt")]
    (io/make-parents wavFilePath)
    (spit textFileName joinedText :encoding "shift-jis")
    (g/record library joinedText wavFilePath)))

(defn save-to-exo
  [library text start]
  (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
        filepath (f/getAbsolutePath (str default-output-path (gen-file-name library joinedText)))
        wavFileName (str filepath ".wav")
        exo-file-name (str filepath ".exo")
        result (save-to-file library text)]
    (spit exo-file-name (parser/yaml->aviutl-object (aviutl/get-tts-object start wavFileName joinedText library)) :encoding "shift-jis")))

(defn save-to-files
  [ttslist]
  (doall (map #(save-to-file (first %) (rest %)) ttslist)))

(defn gen-requests
  [line]
  (-> 
   line 
   (as-> x
         (conj x (f/getAbsolutePath (str "output/voices/" (s/join "_" x) ".wav")))
     (apply g/gen-tts-request x))))

(defn talk-lines
  [lines]
  (map 
   #(map (comp g/talk gen-requests) %)
   lines))

(defn record-lines
  [lines]
  (map
   #(map (comp g/record gen-requests) %)
   lines))