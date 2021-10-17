(ns descript-to-video.core
  (:gen-class)
  (:require [descript-to-video.tts :as tts]
            [descript-to-video.markdown.parser :as mdparser]))



(defn -main
  [& args]
  ;; TODO:引数の値による処理分岐
  ;; とりあえず出力のファイル形式は変えられるようにする
  (apply println "Received args:" args)
  (let [targetPath (cond (string? args) args :else "E://Documents/descript-to-video/sample/sample.md")
        ;; "E://Documents/descript-to-video/sample/sample.md"
        lib-text  (mdparser/get-voiceroid-text-lines (slurp targetPath))]
    (println "result:" lib-text)
    (tts/save-to-files lib-text)
    (shutdown-agents)
  ))