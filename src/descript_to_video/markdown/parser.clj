(ns descript-to-video.markdown.parser
  (:require [clojure.string :as s]))

;; TODO:configファイルから取得するようにする
(def separator "＞")

(defn get-voiceroid-text-lines
  [text]
  (map 
   #(s/split (s/trim %) (re-pattern separator)) 
   (filter 
    #(s/includes? % separator) 
    (s/split-lines text))))

(defn get-body-text-lines
  [text]
  (filter 
   #(not (s/includes? % separator))
   (s/split-lines text)))

(comment
  
  (def raw-text (slurp "resources/manuscripts/sample.md"))
  (println raw-text)
  (map #(s/split % #"＞") (filter #(s/includes? % "＞") (s/split-lines raw-text)))
  (get-voiceroid-text-lines raw-text)
  (get-body-text-lines raw-text)
  )

