(ns descript-to-video.tts
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]))

(def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\x64\\Release\\SpeechSample.exe")

(def separator #"-----\r\n")

(def default-output-path "voices/")

(defn talk
  "TTSライブラリ名とテキストを指定し、SpeechSample.exeで喋らせる"
  [library text]
  (shell/sh "cmd" "/c" ttsControllerPath "-t" text "-n" library))

(defn gen-wav-name
  [library text]
  (str library "_" text ".wav"))

(defn save-to-file
  [library text]
  (shell/sh "cmd" "/c" ttsControllerPath "-t" text "-n" library "-o" (str default-output-path (gen-wav-name library text))))

(defn get-library-list
  "SpeechSample.exeを実行し、結果から使用可能なTTSライブラリの一覧を取得する"
  []
  (let [rawOutput (shell/sh "cmd" "/c" ttsControllerPath "-v" :out-enc "shift-jis")
        exitCode (:exit rawOutput)
        [_ output _] (s/split (:out rawOutput) separator)]
    (cond
      (= exitCode 0) (s/split-lines output)
      :else [])))

(comment
  (def libraries (get-library-list))
  libraries
  (talk "さとうささら" "さとうささらです")
  
  (talk "葵" "さとうささらです")
  (save-to-file "葵" "さとうささらです")

  )