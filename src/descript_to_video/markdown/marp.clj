(ns descript-to-video.markdown.marp
  (:require [clojure.java.shell :as shell]))

(defn export-slides
  [markdown-path]
  (shell/sh "cmd" "/c" (str "yarn marp " markdown-path " --images png --image-scale 1.5"))
  )

(comment
  (shell/sh "cmd" "/c" "yarn marp sample/sample.md --images png --image-scale 1.5")
  (def result (export-slides "E://Documents/descript-to-video/sample/sample.md"))
  (:exit result)
  (println (:err result))
  )