(ns descript-to-video.tts
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]
            [clojure.java.io :as io]))

;; 定数群
;; TODO:設定ファイルから取得
(def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\x64\\Release\\SpeechSample.exe")
;; (def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\x64\\Debug\\SpeechSample.exe")
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
  (shell/sh "cmd" "/c" ttsControllerPath "-t" (str text) "-n" library))

(defn gen-wav-name
  [library text]
  (str library "_" text ".wav"))

(defn save-to-file
  [library text]
  (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
        outFileName (str default-output-path (gen-wav-name library joinedText))]
    (io/make-parents outFileName)
    ;; (println outFileName)
    (shell/sh "cmd" "/c" ttsControllerPath "-t" joinedText "-n" library "-o" outFileName)))



(comment
  (get-library-list)
  (talk "さとうささら" "さとうささらです")

  (talk "葵" "さとうささらです")
  (save-to-file "葵" "葵です")
  (save-to-file "茜" '("あかねちゃんやで" "葵です"))
  (save-to-file "さとうささら" "さとうささらです")

  )