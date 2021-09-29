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

(defn- get-settings
  []
 (yaml/from-file default-settings-path))

(def prefix "<?s=[==[\r\n")
(def suffix "\r\n]==];require(\"PSDToolKit\").prep.init({ls_mgl=0,ls_mgr=0,st_mgl=0,st_mgr=0,sl_mgl=0,sl_mgr=0,},obj,s)?>")

(defn get-template
  "paramに対応するaviUtlのオブジェクトのファイル名を返す"
  [^String templateName]
  (let [settings (get-settings)
        setting (get settings (keyword templateName) (:default settings))]
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
  [path start end]
  (copy-from-object-as-ordered-map
   (map->ordered-map
    {:0 {:start (str start)
         :end (str end)
         :0.0 {:file path}}})
   "slide"))

(defn get-tts-object
  [start path text library]
  (let [end (dec (+ start (a/get-wav-length path)))
        absolute-path (.getAbsolutePath (io/file path))]
    ;; (println "raw text:" text)
    (copy-from-object-as-ordered-map
     (map->ordered-map
      {:0 {:start (str start)
           :end (str end)
           :0.0 {:text (formatter/string->padded-hex (str prefix text suffix))}}
       :1 {:start (str start)
           :end (str end)
           :1.0 {:file absolute-path}}})
     library)))

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
  (println (parser/yaml->aviutl-object raw-slide-template))
  (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")
  (spit "output/slide2.exo" (parser/yaml->aviutl-object (get-slide-object-as-ordered-map "E:\\Videos\\VoiceroidWaveFiles.002.png"  "300" "600")) :encoding "shift-jis")
  (m/deep-merge-with (fn [v1 v2] (println v1 v2) (cond (map? v2) (into (ordered-map) v2) :else v1)) '{:0 {:start "300" :end "600" :0.0 {:file "E:\\Videos\\VoiceroidWaveFiles.002.png"}}})
  (ordered-map slide-replace-map)
  (def sample-wav (io/file "./output/voices/210923210356664_ゆかり_おっつおっつ.wav"))
  (.getAbsolutePath sample-wav)
  (parser/aviutl-object->yaml (slurp (get-template "ゆかり") :encoding "shift-jis"))
  (def vo (get-voice-object 20 (.getAbsolutePath sample-wav) (slurp "./output/voices/210923210356664_ゆかり_おっつおっつ.txt" :encoding "shift-jis") "ゆかり"))
  (println (parser/yaml->aviutl-object vo))
  (formatter/padded-hex->string (formatter/string->padded-hex (str prefix "ゆかりさんでした" suffix)))
  )