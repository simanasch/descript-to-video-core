(ns descript-to-video.handler.websocket
  (:require
   [descript-to-video.tts.tts :as tts]
   [descript-to-video.markdown.parser :as mdparser]
   [descript-to-video.markdown.marp :as marp]
   [descript-to-video.util.file :as f]
   [descript-to-video.aviutl.aviutl :as aviutl]
   [descript-to-video.aviutl.parser :as aviutl-parser]

   [clojure.core.async :refer [<! >! go]]
   [clojure.tools.reader.edn :as edn])
  )

(defn start
  "TODO:処理分割とリネーム
   引数のmarkdown,exoからttsの出力とスライドの出力を行い、引数の.exoファイルにマージしたファイルのパスを返す"
  ([{:keys [markdown-path exo-path]}]
   (let
    [tts-lines (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides (slurp markdown-path)))
     slides (marp/export-slides markdown-path)
     tts-results (tts/record-lines tts-lines)
     result-exo (-> (aviutl-parser/aviutl-object->yaml (slurp exo-path :encoding "shift-jis"))
                    (aviutl/merge-aviutl-objects (aviutl/get-tts-objects (flatten tts-results)))
                    (aviutl/merge-aviutl-objects (aviutl/get-slide-objects slides (aviutl/get-slide-display-positions tts-results))))]
     (println tts-results)
     (println slides)
     (spit
      (str (f/getFolderName exo-path) "\\sample_result.exo")
      (aviutl-parser/yaml->aviutl-object result-exo) :encoding "shift-jis")
     (str (f/getFolderName exo-path) "\\sample_result.exo")))
  ([]
   (start {:exo-path "./sample/sample.exo"
           :markdown-path "./sample/sample.md"}))
  )

(defn websocket-handler [{:keys [ws-channel] :as req}]
  (go (let [{:keys [message]} (<! ws-channel)]
        (println "Received message:" message)
        (println "raw request:\n" req)
        (>! ws-channel (str "hello client from server!" message))
        (>! ws-channel (start (edn/read-string message))))))