(ns descript-to-video.markdown.parser
  (:require [clojure.string :as s]))

;; TODO:configファイルから取得するようにする
(def separator "＞")
(def slide-separator #"---+\r\n")

(defn get-voiceroid-text-lines
  "引数のテキストから、Separatorを含む行をSeparator前後でsplitしたlistを返す"
  [lines]
  (let [has-separator-line (filter #(s/includes? % separator) lines)]
    (map #(s/split (s/trim %) (re-pattern separator)) has-separator-line)))

(defn get-body-text-lines
  "引数のテキストのうち、Separatorを含まない行を返す"
  [lines]
  (filter #(not (s/includes? % separator)) lines))

(defn split-by-slides
  "markdownから動画にする要素をスライドごとに分割する"
  [text]
  (let [text-seq (s/split text slide-separator)
        text-to-split (cond (s/starts-with? text "--") (nnext text-seq)
                            :else text-seq)]
    (map s/split-lines text-to-split)))