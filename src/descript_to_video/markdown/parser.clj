(ns descript-to-video.markdown.parser
  (:require [clojure.string :as s]))

;; TODO:configファイルから取得するようにする
(def separator "＞")

(defn get-voiceroid-text-lines
  "引数のテキストから、Separatorを含む行をSeparator前後でsplitしたlistを返す"
  [text]
  (let [lines (s/split-lines text)
        has-separator-line (filter #(s/includes? % separator) lines)]
    (map #(s/split (s/trim %) (re-pattern separator)) has-separator-line)))

(defn get-body-text-lines
  "引数のテキストのうち、Separatorを含まない行を返す"
  [text]
  (let [lines (s/split-lines text)]
    (filter #(not (s/includes? % separator)) lines)))

(comment
  
  (def raw-text (slurp "resources/manuscripts/sample.md"))
  (println raw-text)
  (map #(s/split % #"＞") (filter #(s/includes? % "＞") (s/split-lines raw-text)))
  (get-voiceroid-text-lines raw-text)
  (get-body-text-lines raw-text)
  )

