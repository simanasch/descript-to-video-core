(ns descript-to-video.aviutl.parser-test
  (:require
   [descript-to-video.aviutl.parser :refer :all]
   [yaml.core :as yaml]
   [flatland.ordered.map :refer :all]))

(comment
  ;; 動作確認に使っているスニペット系
  (def sample-map (aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def sample-tts-object (descript-to-video.aviutl.aviutl/get-tts-object 1  "./output/voices/210923210356664_ゆかり_おっつおっつ.wav" "おっつおっつ" "ゆかり"))
  (def sample-tts-object-2 (descript-to-video.aviutl.aviutl/get-tts-object 1  "./output/voices/210826231845813_葵_葵です.wav" "葵です" "ゆかり"))
  (def sample-tts-merged (concat-aviutl-map sample-map sample-tts-object))
  (map (juxt :layer :start) sample-tts-object)
  (map (apply juxt '[:layer :start]) (:0 sample-tts-object))
  (concat (vals sample-map) (vals sample-tts-object-2))


  (keys (dissoc sample-tts-merged :exedit))
  (def sample-tts-sorted (sort-by (juxt (comp parseint :layer) (comp parseint :start)) (vals sample-tts-merged)))
  (map :layer sample-tts-sorted)
  (map :start sample-tts-sorted)
  ((juxt :layer :start) #(fn [x] (comp parseint (% x))))
  ((comp parseint :layer) (second (vals sample-tts-merged)))

  (def sample-slide1 (aviutl-object->yaml (slurp "./sample/slide_template.exo" :encoding "shift-jis")))
  (def sample-slide2 (aviutl-object->yaml (slurp "./output/slide2.exo" :encoding "shift-jis")))
  ;; sample-yamlとparsedは一致する(はず)
  (def raw-dest (slurp "./sample/sample_dest.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))

  (map list (keys sample-map))
  (map #(list :exedit %) (keys (get-in sample-map '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in sample-map '(:exedit)))))

  (get-in sample-map [:0 :layer])
  (get-in sample-tts-object [:0 :layer])
  (->
   sample-tts-object
  ;;  sample-map
  ;;  (get-in [:0 :start])
   (get-in [:0 :layer])
   Integer/parseInt)
  (get-in sample-tts-object [:0 :start]))