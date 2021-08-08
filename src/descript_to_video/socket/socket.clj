(ns descript-to-video.socket.socket
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import [java.io RandomAccessFile]))

(defn write-to-pipe
  [^RandomAccessFile pipe ^String text]
  (do
   (.write pipe (.getBytes text))
   (.readLine pipe)))

(comment
  (def pipe (atom (io/file "//./pipe/testpipe" )))
  (def pipe (atom (RandomAccessFile. "//./pipe/testpipe" "rw")))
  (def writer (atom (io/writer "//./pipe/testpipe")))

  (def reader (atom (io/reader "//./pipe/testpipe")))

  (.read @reader)
  (.readLine @pipe)

  (.write @writer "おはようございます\n")
  (.write @pipe (.getBytes "おはようございます\n"))
  (write-to-pipe @pipe "おはようございます")

  (.flush @writer)

  (.write @writer "葵ちゃんだよ\n")
  (.write @pipe (.getBytes "葵ちゃんだよ\n"))

  (.flush @writer)

  (.write @writer "これはサンプルのテキストです\n")

  (.close @writer)
  (.close @pipe)

  (.write @writer "end")

  (.newLine @writer)
  )



;; (with-open [rdr (io/reader "//./pipe/testpipe") ]
;;    (printf "%s\n" (s/join "\n" (line-seq rdr))))