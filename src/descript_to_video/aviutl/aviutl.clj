(ns descript-to-video.aviutl.aviutl
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :only join]
            [descript-to-video.util.format :as formatter]
            ;; [instaparse.core :as insta]
            [yaml.core :as yaml]))

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