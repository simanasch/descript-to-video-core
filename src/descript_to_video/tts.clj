(ns descript-to-video.tts
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]
            [clojure.java.io :as io]))

;; 定数群
;; TODO:設定ファイルから取得
(def ttsControllerPath "%USERPROFILE%\\TTSController\\src\\SpeechSample\\bin\\x64\\Release\\SpeechSample.exe")
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

(defn talk-agent
  [library text]
  (let [ag (agent library)]
    (send-off ag #(talk %1 %2) text)))

(defn gen-wav-name
  [library text]
  (str library "_" text ".wav"))

(defn save-to-file
  [library text]
  (let [joinedText (cond (seq? text) (s/join "。" text) :else text)
        outFileName (str default-output-path (gen-wav-name library joinedText)) ]
    (io/make-parents outFileName)
    (println outFileName)
    (shell/sh "cmd" "/c" ttsControllerPath "-t" joinedText "-n" library "-o" outFileName)))

(defn save-to-file-agent
  [library text]
  (let [ag (agent library)]
    (send-off ag #(save-to-file %1 %2) text)))

(comment
  (def sample-texts '(["ゆかり" "これはサンプルのスライドです"] ["ゆかり" "解説はコメントに書くようにしています、"] ["ゆかり" "テキストを書いてコマンド実行すると、"] ["ゆかり" "コメントの中からvoiceroidで読み上げるテキストを拾って"] ["ゆかり" "読み上げが終わったら次のスライドを表示するようにしてくれます"] ["ゆかり" "biim風のレイアウトにもできます"]))
  (def talk-task (talk-agent "葵" "さとうささらです"))
  (def ag (agent "葵"))
  (send-off ag #(talk %1 %2)  "さとうささらです")
  (await-for 10000 ag)
  (await-for 10000 talk-task)
  (let [ag (talk-agent "茜" "茜ちゃんやで")]
    (await-for 10000 ag))
  (let [ag (save-to-file-agent "茜" "茜ちゃんやで")]
    (await-for 10000 ag))

  (talk "葵" "さとうささらです")
  (save-to-file "葵" "葵です")
  (save-to-file "茜" '("あかねちゃんやで" "葵です"))
  (save-to-file "さとうささら" "さとうささらです")

  )