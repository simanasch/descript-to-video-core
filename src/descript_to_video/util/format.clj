(ns descript-to-video.util.format
  (:require [clojure.string :only replace]))


(defn string->hex
  "StringをUTF-16LEとして受け取りhexの値を返す"
  [s]
  (format "%x" (new java.math.BigInteger (.getBytes s "UTF-16LE"))))

(defn pad-str
  [encoded]
   (take (- 4096 (count encoded)) (repeat "0")))

(defn string->padded-hex
  [s]
  (apply str (string->hex s) (pad-str (string->hex s))))

(defn remove-pad-str
  [hex]
  (clojure.string/replace hex #"(0{4})+$" ""))

(defn hex->string
  "hexの値からStringを返す"
  [s]
  (let [bytes (into-array Byte/TYPE
                          (map (fn [[x y]]
                                 (unchecked-byte (Integer/parseInt (str x y) 16)))
                               (partition 2 s)))]
    (String. bytes "UTF-16LE")))

(defn padded-hex->string
  [s]
  (hex->string (remove-pad-str s)))