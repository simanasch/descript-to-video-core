(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
            [descript-to-video.markdown.parser :as mdparser]
            [descript-to-video.grpc.client :as client]
            [descript-to-video.markdown.marp :as marp]
            [descript-to-video.util.file :as f]
            [descript-to-video.aviutl.aviutl :as aviutl]
            [descript-to-video.aviutl.parser :as aviutl-parser]))
  ;; [descript-to-video.grpc.service]
  ;; (:import [io.grpc Server ServerBuilder]
  ;;          [io.grpc.stub StreamObserver]
  ;;          [descript-to-video.grpc.service GreeterServiceImpl]))

;; (def SERVER_PORT 30051)

;; (defn start []
;;   (let [greeter-service (new GreeterServiceImpl)
;;         server (-> (ServerBuilder/forPort SERVER_PORT)
;;                    (.addService greeter-service)
;;                    (.build)
;;                    (.start))]
;;     (-> (Runtime/getRuntime)
;;         (.addShutdownHook
;;          (Thread. (fn []
;;                     (if (not (nil? server))
;;                       (.shutdown server))))))
;;     (if (not (nil? server))
;;       (.awaitTermination server))))

(defn start
  [& args]
  (let [template-path "E://Documents/descript-to-video/sample/sample.exo"
        markdown (cond (string? args) args :else "E://Documents/descript-to-video/sample/sample.md")
        ;; "E://Documents/descript-to-video/sample/sample.md"
        tts-lines  (map mdparser/get-voiceroid-text-lines (mdparser/split-by-slides (slurp markdown)))
        slides (marp/export-slides markdown)
        ;; slide-joined (map aviutl/get-slide-object-as-ordered-map slides)
        tts-results (tts/record-lines tts-lines)
        result-exo (-> (aviutl-parser/aviutl-object->yaml (slurp template-path :encoding "shift-jis"))
                            (aviutl/merge-aviutl-objects (aviutl/get-tts-objects (flatten tts-results)))
                            (aviutl/merge-aviutl-objects (aviutl/get-slide-objects slides (aviutl/get-slide-display-positions tts-results))))]
    ;; TODO:別スレッドでの処理に移動

    (println tts-results)
    (println slides)
    (println (str (f/getFolderName template-path) "\\sample_result.exo"))
    (spit
     (str (f/getFolderName template-path) "\\sample_result.exo")
     (aviutl-parser/yaml->aviutl-object result-exo) :encoding "shift-jis"))
  )

(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  (apply println "Received args:" args)
  ;; (print "Now listening on port " SERVER_PORT)
  (start args)
  ;; mainでの呼び出しの場合は処理終了時に接続を切る
  (client/shutdown-connection!))