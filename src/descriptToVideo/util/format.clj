(ns descriptToVideo.util.format)

(defn hexify
  "テキストをUTF-16LEとして受け取りhexの値を返す"
  [s]
  (format "%x" (new System.Numerics.BigInteger (.getBytes s "UTF-16LE"))))