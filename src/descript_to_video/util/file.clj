(ns descript-to-video.util.file
  (:require [clojure.java.io :as f]))

(defn getAbsolutePath
  "引数のパス文字列から絶対パスを返す"
  [path]
  (.getAbsolutePath (f/as-file path)))

(defn getFolderName
  [path]
  (.getParent (f/as-file path)))