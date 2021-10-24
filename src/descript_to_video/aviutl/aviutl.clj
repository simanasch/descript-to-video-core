(ns descript-to-video.aviutl.aviutl
  (:gen-class)
  (:require [clojure.java.io :as io]
            [descript-to-video.util.format :as formatter]
            [descript-to-video.util.map :as m]
            [descript-to-video.util.audio :as a]
            [descript-to-video.util.file :as f]
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

(defn get-slide-object-as-ordered-map
  ([path start end]
   (copy-from-object-as-ordered-map
    (m/map->ordered-map
     {:0 {:start (str start)
          :end (str end)
          :0.0 {:file path}}})
    "slide"))
  ([path] (get-slide-object-as-ordered-map path 1 150)))

(defn get-tts-object
  ([start path text library]
   (let [end (dec (+ start (a/get-wav-length path)))
         absolute-path (f/getAbsolutePath path)
         layer (get-in setting [(keyword library) :layer])]
    ;; (println "raw text:" setting (keyword library) layer)
     (copy-from-object-as-ordered-map
      (m/map->ordered-map
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
   (let [absolute-path (f/getAbsolutePath path)
         layer (get-in setting [(keyword library) :layer])]
     (copy-from-object-as-ordered-map
      (m/map->ordered-map
       {:0 {:start "1"
            :layer layer
            :0.0 {:text (formatter/string->padded-hex (str prefix text suffix))}}
        :1 {:layer (inc layer)
            :1.0 {:file absolute-path}}})
      library))))

(defn get-tts-objects
  [template-path tts-results]
  (loop [start 1
         res (flatten tts-results)
         result (parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))]
    (cond
      (empty? res) result
      :else
      (let [ttsResult (first res)
            obj (get-tts-object start (:outputPath ttsResult) (:Body ttsResult) (:libraryName ttsResult))]
        (recur (+ start (a/get-wav-length (:outputPath ttsResult)))
               (rest res)
               (parser/concat-aviutl-map
                result
                obj))))))