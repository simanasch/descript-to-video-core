(ns descript-to-video.util.audio
  (:require [clojure.java.io :as io])
  (:import [javax.sound.sampled AudioSystem]))

(defn get-wav-length
  ([path frameRate]
  (try
    (with-open [fileStream (io/input-stream path)
                wavStream (AudioSystem/getAudioInputStream fileStream)]
      (int
       (/
        (* frameRate (.getFrameLength wavStream))
        (.getSampleRate (.getFormat wavStream)))))
    (catch Exception e
      (println "caught Exception:" (str (.getMessage e)))
      0)))
  ([path] (get-wav-length path 30)))