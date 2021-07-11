(ns descript-to-video.aviutl.aviutl
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :only join]
            [descript-to-video.util.format :as formatter]
            [instaparse.core :as insta]))

(defn get-template
  "paramに対応するaviUtlのオブジェクトのファイル名を返す
   TODO:ファイル名を設定ファイルとかディレクトリ内のalias名から取得するようにする"
  [template]
  (cond (= template "hoge") "fuga"
        :else "resources/alias/default.exo"))

(defn set-object-body
  "TODO:Mapを使った実装にする?"
  [body key seq]
  (map
   #(if (= %1 key) (str %1 (formatter/string->padded-hex body)) %1) ;;
   seq))

(defn gen-object
  "templateをコピーし、表示するテキストがtextのオブジェクトを生成する
   TODO:ファイル名を変数にする、処理をきれいにする、set-object-bodyあたりを分割する"
  [text template & rest]
  (with-open [r (io/reader (get-template template) :encoding "shift-jis")
              o (io/writer "test.exo" :encoding "shift-jis")]
    (doseq [body (reduce conj [] (set-object-body text "text=" (line-seq r)))]
      (.write o (str body "\r\n")))))