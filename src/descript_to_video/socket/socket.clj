(ns descript-to-video.socket.socket
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(def writer (atom (io/writer "//./pipe/testpipe") ))
;; (def reader (atom (io/reader "//./pipe/testpipe") ))
;; (.read @reader)
(.write @writer "おはようございます\n")
(.flush @writer)
(.write @writer "葵ちゃんだよ\n")
(.flush @writer)
(.write @writer "これはサンプルのテキストです\n")
(.close @writer)
;; (.write @writer "end")
;; (.newLine @writer)

;; (with-open [rdr (io/reader "//./pipe/testpipe") ]
;;    (printf "%s\n" (s/join "\n" (line-seq rdr))))