(ns descript-to-video.aviutl.aviutl
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.walk :as w]
            [descript-to-video.util.format :as formatter]
            [descript-to-video.util.map :as m]
            [descript-to-video.util.audio :as a]
            [descript-to-video.aviutl.parser :as parser]
            [yaml.core :as yaml])
  (:refer  flatland.ordered.map))
;; (:refer clojure.edn)

(def default-settings-path "resources/settings.yaml")

(def setting (yaml/from-file default-settings-path))

(def prefix "<?s=[==[\r\n")
(def suffix "\r\n]==];require(\"PSDToolKit\").prep.init({ls_mgl=0,ls_mgr=0,st_mgl=0,st_mgr=0,sl_mgl=0,sl_mgr=0,},obj,s)?>")

(defn get-template
  "paramに対応するaviUtlのオブジェクトのファイル名を返す"
  [^String templateName]
  (let [setting (get setting (keyword templateName) (:default setting))]
    (:alias setting)))

(defn set-object-body
  "TODO:Mapを使った実装にする?"
  [body key seq]
  (map
   #(if (= %1 key) (str %1 (formatter/string->padded-hex (str prefix body suffix))) %1)
   seq))

(defn gen-object
  "templateをコピーし、表示するテキストがtextのオブジェクトを生成する
   TODO:ファイル名を変数にする、処理をきれいにする、set-object-bodyあたりを分割する"
  [text template & rest]
  (with-open [r (io/reader (get-template template) :encoding "shift-jis")
              o (io/writer "test.exo" :encoding "shift-jis")]
    (doseq [body (reduce conj [] (set-object-body text "text=" (line-seq r)))]
      (.write o (str body "\r\n")))))

(defn copy-from-object-as-ordered-map
  "templateをコピーし、新しいobjectを生成して返す
   updates:更新内容のordered-map"
  [updates templateName]
  (let [template (get-template templateName)
        templateObject (parser/aviutl-object->yaml (slurp template :encoding "shift-jis"))]
    ;; (println templateObject)
    (m/deep-merge-with
     (fn [v1 v2] (cond (nil? v2) v1 :else v2))
     templateObject updates)))

(defn map->ordered-map
  [map]
  (w/postwalk #(cond (map? %) (ordered-map %) :else %) map))

(defn get-slide-object-as-ordered-map
  ([path start end]
   (copy-from-object-as-ordered-map
    (map->ordered-map
     {:0 {:start (str start)
          :end (str end)
          :0.0 {:file path}}})
    "slide"))
  ([path] (get-slide-object-as-ordered-map path 1 150)))

(defn get-tts-object
  ([start path text library]
   (let [end (dec (+ start (a/get-wav-length path)))
         absolute-path (.getAbsolutePath (io/file path))
         layer (get-in setting [(keyword library) :layer])]
    (println "raw text:" setting (keyword library) layer)
     (copy-from-object-as-ordered-map
      (map->ordered-map
       {:0 {:layer layer
            :start (str start)
            :end (str end)
            :0.0 {:text (formatter/string->padded-hex (str prefix text suffix))}}
        :1 {:layer (inc layer)
            :start (str start)
            :end (str end)
            :1.0 {:file absolute-path}}})
      library)))
  ([path library text]
   (let [absolute-path (.getAbsolutePath (io/file path))
         layer (get-in setting [(keyword library) :layer])]
     (copy-from-object-as-ordered-map
      (map->ordered-map
       {:0 {:start "1"
            :layer layer
            :0.0 {:text (formatter/string->padded-hex (str prefix text suffix))}}
        :1 {:layer (inc layer)
            :1.0 {:file absolute-path}}})
      library))))

;; 動作確認用のスクリプト類
;; TODO:テストに移動
(comment
  (get-template "ゆかり")
  (slurp (get-template "ゆかり") :encoding "shift-jis")
  (gen-object "ゆかりさんです" "ゆかり")
  ;; (parser/aviutl-object->yaml)
  (def slide-replace-map '{:0 {:start "0" :end "300" :0.0 {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"}}})
  (def raw-slide-template (parser/aviutl-object->yaml (slurp (get-template "slide") :encoding "shift-jis")))
  ;; slideの.exoで更新する内容の期待値
  (def slide-update-map (ordered-map {:0 {:start "300" :end "600" :0.0 (ordered-map {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"})}}))
  (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")
  (spit "output/slide2.exo" (parser/yaml->aviutl-object (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")) :encoding "shift-jis")
  (m/deep-merge-with (fn [v1 v2] (println v1 v2) (cond (map? v2) (into (ordered-map) v2) :else v1)) '{:0 {:start "300" :end "600" :0.0 {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"}}})
  (def sample-wav (io/file "./output/voices/210923210356664_ゆかり_おっつおっつ.wav"))
  (def sample-exo (get-tts-object 1  "./output/voices/210923210356664_ゆかり_おっつおっつ.wav" "おっつおっつ" "ゆかり"))
  (-> sample-exo
      (get-in '[:0 :layer]))
  (.getAbsolutePath sample-wav)
  (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix)))

  (require '[descript-to-video.markdown.parser :as mdparser])
  (def sample-map (parser/aviutl-object->yaml (slurp "./sample/sample.exo" :encoding "shift-jis")))
  (def sample-file-path "sample/sample.md")
  ;; (def memo (slurp "../memo.md"))
  (def raw-text (slurp sample-file-path))
  (def sample-slide-lines (mdparser/split-by-slides raw-text))
  (def sample-tts-lines (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides raw-text)))

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
  (def sample-slildes-and-tts-lines
    (interleave sample-slide-lines sample-tts-lines))


  (get (first (first sample-tts-objects)) :exedit)
  (get (first (reduce concat sample-tts-objects)) :exedit)
  ;; slide-mergedとtts-mergedをテンプレにした.exoにマージすればとりあえず目標達成になるはず
  (def slide-merged
    (reduce
     parser/concat-aviutl-map
     sample-map
     sample-slides-objects))
  (def tts-merged
    (reduce parser/concat-aviutl-map
            sample-map
            sample-tts-objects))
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
  (spit "../tmp_tts.exo" (parser/yaml->aviutl-object tts-merged) :encoding "shift-jis")
  (spit "../tmp_slides-and-tts.exo" (parser/yaml->aviutl-object slide-and-tts-merged) :encoding "shift-jis")
  (spit "../tmp_slides-and-tts.yaml"  (yaml.writer/generate-string slide-and-tts-merged) :encoding "shift-jis")
  )