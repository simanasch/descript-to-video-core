(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
            [descript-to-video.markdown.parser :as mdparser]))



(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  ;; とりあえず出力のファイル形式は変えられるようにする
  (apply println "Received args:" args)
  (let [targetPath (read-line)
        ;; "E://Documents/descript-to-video/sample/sample.md"
        lib-text  (mdparser/get-voiceroid-text-lines (slurp targetPath))]
    (println "result:" lib-text)
    (tts/save-to-files lib-text)
    (shutdown-agents)
  ))

(comment
  ;; 以下動作確認してる時のサンプル
  (def lib-text  (mdparser/get-voiceroid-text-lines (slurp "E://Documents/descript-to-video/sample/sample.md")))
  lib-text
  (first lib-text)
  (-main)
  (tts/talk (first (first lib-text)) (rest (first lib-text)))
  (for [sentence lib-text
        :let [library (first sentence)
              text (rest sentence)
              ag (tts/save-to-file-agent library text)]]
    (await-for 10000 ag))

  (map #(tts/save-to-file (first %)  (rest %)) lib-text)
  )