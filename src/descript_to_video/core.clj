(ns descript-to-video.core
  (:require [clojure.string :only (join)])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn hexify
  "テキストをUTF-16LEとして受け取りhexの値を返す"
  [s]
  (format "%x" (new java.math.BigInteger (.getBytes s "UTF-16LE"))))

(defn format-text-as-aviutl-object
  "引数のテキストをaviUtl拡張編集の.obj形式のテキストにする
   テキストをUTF-16LEでエンコードした場合のbinary値を、lengthが4096になるまで右側に0詰めする"
  [text]
  (let [encoded (hexify text)
        pad (take (- 4096 (count encoded)) (repeat "0"))]
    (apply str encoded pad)))