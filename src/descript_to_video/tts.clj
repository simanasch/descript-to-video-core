(ns descript-to-video.tts
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]))

(def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\x64\\Release\\SpeechSample.exe")

(def separator #"-----\r\n")

(defn talk
  "TTSライブラリ名とテキストを指定し、SpeechSample.exeで喋らせる"
  [Library text]
  (shell/sh "cmd" "/c" ttsControllerPath "-t" text "-n" Library))

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
  (talk "さとうささら" "さとうささらです")
  (talk "葵" "さとうささらです")

  )