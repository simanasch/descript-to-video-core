(ns descript-to-video.core
  (:gen-class)
  (:require
  ;;  [compojure.core :refer [routes]]
  ;;  [ring.adapter.jetty :as server]
  ;; [descript-to-video.handler.main :refer [main-routes]]
   [descript-to-video.tts.tts :as tts]
   [descript-to-video.markdown.parser :as mdparser]
   [descript-to-video.grpc.client :as client]
   [descript-to-video.markdown.marp :as marp]
   [descript-to-video.util.file :as f]
   [descript-to-video.aviutl.aviutl :as aviutl]
   [descript-to-video.aviutl.parser :as aviutl-parser]
  ;;  [descript-to-video.middleware :refer [wrap-dev]]
   ))
  ;; [descript-to-video.grpc.service]
  ;; (:import [io.grpc Server ServerBuilder]
  ;;          [io.grpc.stub StreamObserver]
  ;;          [descript-to-video.grpc.service GreeterServiceImpl]))

(defn start
  ([args]
   (let [{:keys [markdown-path exo-path]} args]
     (println markdown-path)
     (println exo-path)
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
       (str (f/getFolderName exo-path) "\\sample_result.exo"))))
  ([]
   (start {:exo-path "./sample/sample.exo"
           :markdown-path "./sample/sample.md"}))
  )

(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  (apply println "Received args:" args)
  ;; (print "Now listening on port " SERVER_PORT)
  (start args)
  ;; mainでの呼び出しの場合は処理終了時に接続を切る
  (client/shutdown-connection!))