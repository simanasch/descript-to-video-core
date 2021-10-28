(ns descript-to-video.aviutl.aviutl-test
  (:require
   [clojure.test :refer :all]
   [descript-to-video.aviutl.aviutl :refer :all]
   [clojure.java.io :as io]
   [descript-to-video.util.format :as formatter]
   [descript-to-video.util.map :as m]
   [descript-to-video.aviutl.parser :as parser]
   [flatland.ordered.map :refer :all]
   [descript-to-video.markdown.parser :as mdparser]
   [descript-to-video.tts :as tts]
   [descript-to-video.util.audio :as a]
   [descript-to-video.markdown.marp :as marp]))

(def sample-file-path "test/resources/test_sample.md")
(def sample-template-path "test/resources/test_sample.exo")

(deftest descript-to-video.aviutl.aviutl-test
  (testing "get-templateのテスト"
    (testing "設定値がある場合"
      (is (=
           (get-in setting [:ゆかり :alias])
           (get-template "ゆかり"))))
    (testing "設定値がない(defaultを返す)場合"
      (is (=
           (get-in setting [:default :alias])
           (get-template "ぬかり")))))
  (testing "gen-objectのテスト")
  (testing "formatterのテスト"
    (is (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix))))))

(comment
  (run-tests)
  (require '[descript-to-video.aviutl.aviutl :refer :all] :reload)
  (slurp (get-template "ゆかり") :encoding "shift-jis")
  ;; slideの.exoで更新する内容の期待値
  (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")
  (spit "output/slide2.exo" (parser/yaml->aviutl-object (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")) :encoding "shift-jis")
  (m/deep-merge-with (fn [v1 v2] (println v1 v2) (cond (map? v2) (into (ordered-map) v2) :else v1)) '{:0 {:start "300" :end "600" :0.0 {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"}}})
  (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix)))

  (def sample-map (parser/aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def raw-text (slurp sample-file-path))
  (def sample-slide-lines (mdparser/split-by-slides raw-text))
  (def tts-lines (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides raw-text)))
  (def tts-results (tts/record-lines tts-lines))
  (def tts-joined (merge-tts-objects
                   (parser/aviutl-object->yaml (slurp sample-template-path :encoding "shift-jis"))
                   (get-tts-objects (flatten tts-results))))
  (spit
   "../test.exo"
   (parser/yaml->aviutl-object tts-joined) :encoding "shift-jis")
  (map #(get-in % '[:0 :end]) tts-objects)

  (map (comp count first) tts-results)
  (map count (flatten tts-results))
  (defn gen-temp-yaml
    [markdownPath templatePath]
    (let [template (parser/aviutl-object->yaml (slurp templatePath :encoding "shift-jis"))
          raw-text (slurp markdownPath)
          slide-lines (mdparser/split-by-slides raw-text)
          tts-lines (map mdparser/get-voiceroid-text-lines slide-lines)]
      (let [slide-objects (for [serial (map inc (range (count slide-lines)))
                                :let [temp-file-path (str markdownPath "." serial ".png")]]
                            (get-slide-object-as-ordered-map temp-file-path))
            tts-objects (for [lines tts-lines]
                          (reduce concat (map #(get-tts-object (str %) (first %) (rest %)) lines)))]
        (reduce parser/concat-aviutl-map (reduce parser/concat-aviutl-map template slide-objects) tts-objects))))
  (def temp-yaml (gen-temp-yaml "sample/sample.md" "./sample/sample.exo"))
  (spit "../temp-object.exo" (parser/yaml->aviutl-object temp-yaml) :encoding "shift-jis")

  (get-slide-object-as-ordered-map sample-file-path)
  (def sample-slides-objects
    ;; 以下スライドの表示要素に対する.exoをまとめて生成
    (for [serial (map inc (range (count sample-slide-lines)))
          :let [temp-file-path (str sample-file-path "." serial ".png")]]
      (get-slide-object-as-ordered-map temp-file-path)))
  (def sample-tts-objects
    ;; 以下スライドのtts内容に対する.exoをまとめて生成
    (for [lines sample-tts-lines]
      (reduce concat (map #(get-tts-object (str %) (first %) (rest %)) lines))))

  ;; slide-mergedとtts-mergedをテンプレにした.exoにマージすればとりあえず目標達成になるはず
  (def slide-merged
    (reduce
     parser/concat-aviutl-map
     sample-map
     sample-slides-objects))
  (def slide-and-tts-merged
    (reduce parser/concat-aviutl-map
            slide-merged
            sample-tts-objects))
  (->
   (parser/concat-aviutl-map sample-map (first (reduce concat sample-tts-objects)))
  ;;  keys
   parser/yaml->aviutl-object
   println)

  ;; slideに関してはうまくマージできてるっぽい
  (spit "../tmp_slides.exo" (parser/yaml->aviutl-object slide-merged) :encoding "shift-jis")
  ;; ttsもできた
  (spit "../tmp_slides-and-tts.exo" (parser/yaml->aviutl-object slide-and-tts-merged) :encoding "shift-jis")
  (spit "../tmp_slides-and-tts.yaml"  (yaml.writer/generate-string slide-and-tts-merged) :encoding "shift-jis")

  (def lib-text  (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides raw-text)))
  (def tts-results (tts/record-lines lib-text))
  (def template-path "E://Documents/descript-to-video/sample/sample.exo")
  (def tts-joined (get-tts-objects template-path  tts-results))
  (loop [start 1
         res  (flatten tts-results)
         result (parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))]
    (println (empty? res))
    (cond (empty? res) result
          :else
          (let [ttsResult (first res)
                obj (get-tts-object start (:outputPath ttsResult) (:Body ttsResult) (:libraryName ttsResult))]
            (println (:outputPath ttsResult) ttsResult)
            (recur (dec (+ start (a/get-wav-length (:outputPath ttsResult))))
                   (rest res)
                   (parser/concat-aviutl-map
                    result
                    obj)))))
  (seq? tts-results)
  (rest tts-results)
  (first tts-results)
  (let [start 1
        ttsResult (first tts-results)
        result (parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))]
    (println (dec (+ start (a/get-wav-length (:outputPath ttsResult)))))
    ;; (rest tts-results)
    (parser/concat-aviutl-map
     result
     (get-tts-object start (:outputPath ttsResult) (:Body ttsResult) (:libraryName ttsResult)))))