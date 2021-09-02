(ns descript-to-video.util.audio
  (:require [clojure.java.io :as io])
  (:import [javax.sound.sampled AudioSystem]))

(defn get-wav-length
  [path frameRate]
  (try
    (let [wavFile (io/as-file path)
          wavStream (AudioSystem/getAudioInputStream wavFile)]
      (int
       (/
        (* frameRate (.getFrameLength wavStream))
        (.getSampleRate (.getFormat wavStream)))))
    (catch Exception e 
      (println (str "caught exception: " (.getMessage e)))
      0)))