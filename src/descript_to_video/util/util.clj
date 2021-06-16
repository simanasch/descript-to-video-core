(ns descript-to-video.util)

(defn hexify
  "テキストをUTF-16LEとして受け取りhexの値を返す"
  [s]
  (format "%x" (new java.math.BigInteger (.getBytes s "UTF-16LE"))))