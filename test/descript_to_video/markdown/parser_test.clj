(ns descript-to-video.markdown.parser-test
  (:require [clojure.test :refer :all]
            [descript-to-video.markdown.parser :refer :all]
            [clojure.string :as s]))

(comment

  (require '[descript-to-video.util.file :as f])
  (def memo (slurp "../memo.md"))

  (def raw-text (slurp "sample/sample.md"))
  (get-voiceroid-text-lines (first (split-by-slides memo)))
  (map (comp descript-to-video.tts/save-to-files get-voiceroid-text-lines)  (split-by-slides raw-text))
  (def tts-lines (map  get-voiceroid-text-lines  (split-by-slides raw-text)))
  (def sample-line ["ゆかり" "これはテストです"])
  ;; tts-linesに以下をmapすればいいかんじになりそう
  (defn record-line
    [vec]
    (->
     vec
     (as-> x
           (conj x (f/getAbsolutePath (str "output/voices/" (s/join "_" x) ".wav")))
       (apply descript-to-video.grpc.client/gen-tts-request x))
     descript-to-video.grpc.client/record))

  ;; 以下は動作する
  (map
   #(map record-line %)
  ;;  println
   tts-lines)

  ;; 1行だけの場合だと以下も動作
  (descript-to-video.grpc.client/record (descript-to-video.grpc.client/gen-tts-request "ゆかり" "これはテストです" ""))
  (descript-to-video.grpc.client/reset-connection!)
  (descript-to-video.grpc.client/shutdown-connection!)

  (map #(s/split % #"＞") (filter #(s/includes? % "＞") (s/split-lines raw-text)))
  (map get-voiceroid-text-lines (split-by-slides raw-text))
  (get-body-text-lines raw-text)
  )