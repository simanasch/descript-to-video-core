(ns descript-to-video.core-test
  (:require [clojure.test :refer :all]
            [descript-to-video.core :refer :all] :reload-all))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(comment
  ;; 以下動作確認してる時のサンプル
  ;; (def lib-text  (mdparser/get-voiceroid-text-lines (line-seq (slurp "E://Documents/descript-to-video/sample/sample.md"))))
  (-main)
  (start)
  ;; (tts/talk (first (first lib-text)) (rest (first lib-text)))
  ;; (for [sentence lib-text
  ;;       :let [library (first sentence)
  ;;             text (rest sentence)
  ;;             ag (tts/save-to-file-agent library text)]]
  ;;   (await-for 10000 ag))

  ;; (map #(tts/save-to-file (first %)  (rest %)) lib-text)
  )