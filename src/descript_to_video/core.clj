(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
            [descript-to-video.markdown.parser :as mdparser]))



(defn -main
  [& args]
  (apply println "Received args:" args)
  (let [targetPath (read-line)
        ;; "E://Documents/descript-to-video/resources/manuscripts/sample.md"
        lib-text  (mdparser/get-voiceroid-text-lines (slurp targetPath))]
    ;; tts/save-to-fileは別スレッドを立ち上げるのでdoAllで実行待機させる
    (doall (map #(tts/save-to-file (first %)  (rest %)) lib-text))
    (println "result:" lib-text)
    ;; tts/save-to-fileで立ち上げたスレッドを落とす
    (shutdown-agents)))

(comment
  ;; 以下動作確認してる時のサンプル
  (def lib-text  (mdparser/get-voiceroid-text-lines (slurp "E://Documents/descript-to-video/resources/manuscripts/sample.md")))
  lib-text
  (first lib-text)
  (tts/talk (first (first lib-text)) (rest (first lib-text)))
  (for [sentence lib-text
        :let [library (first sentence)
              text (rest sentence)
              ag (tts/save-to-file-agent library text)]]
    (await-for 10000 ag))

  (map #(tts/save-to-file (first %)  (rest %)) lib-text)
  )