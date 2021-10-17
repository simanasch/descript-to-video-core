(ns descript-to-video.markdown.parser-test
  (:require [clojure.test :refer :all]
            [descript-to-video.markdown.parser :refer :all]
            [clojure.string :as s]))

(comment

  (def memo (slurp "../memo.md"))
  (def raw-text (slurp "sample/sample.md"))

  (get-voiceroid-text-lines (first (split-by-slides memo)))
  (map (comp descript-to-video.tts/save-to-files get-voiceroid-text-lines)  (split-by-slides raw-text))
  (s/split raw-text #"^--+")
  (re-matches #"(marp)" raw-text)

  (map #(s/split % #"＞") (filter #(s/includes? % "＞") (s/split-lines raw-text)))
  (map get-voiceroid-text-lines (split-by-slides raw-text))
  (get-body-text-lines raw-text)
  )