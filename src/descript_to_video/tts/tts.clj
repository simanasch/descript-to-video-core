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
  "@deprecated
   SpeechSample.exeを実行し、結果から使用可能なTTSライブラリの一覧を取得する
   TODO:grpc実装のほうに移動"
  {:deprecated true}
  []
  (let [rawOutput (shell/sh "cmd" "/c" ttsControllerPath "-v" :out-enc "shift-jis")
        exitCode (:exit rawOutput)
        [_ output _] (s/split (:out rawOutput) separator)]
    (cond
      (= exitCode 0) (s/split-lines output)
      :else [])))

(defn gen-file-name
  "タイムスタンプ、ttsのライブラリ名、テキスト内容を連結して保存時のファイル名を生成する"
  [[library text] path]
  (str path (d/get-time-stamp) "_" library "_" text ".wav"))

;; (defn save-to-file
;;   "2引数の場合:ttsライブラリ名とテキストを引数に、ファイル名を生成して録音する
;;    3引数の場合:引数のファイル名を使用"
;;   ([library text]
;;   (save-to-file library text (f/getAbsolutePath (str default-output-path (gen-file-name library (cond (seq? text) (s/join "。" text) :else text))))))
;;   ([library text path]
;;    (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
;;         wavFilePath (str path ".wav")
;;         textFileName (str path ".txt")]
;;     (io/make-parents wavFilePath)
;;     (spit textFileName joinedText :encoding "shift-jis")
;;     (g/record library joinedText wavFilePath))))

;; (defn save-to-exo
;;   [library text start]
;;   (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
;;         filepath (f/getAbsolutePath (str default-output-path (gen-file-name library joinedText)))
;;         wavFileName (str filepath ".wav")
;;         exo-file-name (str filepath ".exo")
;;         result (save-to-file library text)]
;;     (spit exo-file-name (parser/yaml->aviutl-object (aviutl/get-tts-object start wavFileName joinedText library)) :encoding "shift-jis")))

(defn- gen-requests
  ([line] (gen-requests line default-output-path))
  ([line path]
   (->
    line
    (as-> x
          (conj x (f/getAbsolutePath (gen-file-name x path)))
      (apply g/gen-tts-request x)))))

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