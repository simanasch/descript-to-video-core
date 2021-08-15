(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
           [descript-to-video.markdown.parser :as mdparser]))



(defn -main
  [& args]
  (apply println "Received args:" args))

(comment
  ;; 以下動作確認してる時のサンプル
  (def lib-text  (mdparser/get-voiceroid-text-lines (slurp "resources/manuscripts/sample.md")))
  (first lib-text)
  (tts/talk (first lib-text) (rest lib-text))
  (map #(tts/save-to-file (first %)  (rest %)) lib-text)
  )