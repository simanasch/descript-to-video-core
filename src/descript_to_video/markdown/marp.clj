(ns descript-to-video.markdown.marp
  (:require [clojure.java.shell :as shell]
            [clojure.string :as s]
            [descript-to-video.util.file :as f]))

(defn export-slides
  "yarnからmarpを呼び出し、引数のmarkdown-pathにあるmarkdownファイルを複数枚画像にする
   戻り値:出力したスライドの絶対パスのリスト 失敗していた場合は空リスト"
  [markdown-path]
  (let [result (shell/sh "cmd" "/c" (str "npx marp " markdown-path " --images png --image-scale 1.5"))
        errors (keep #(re-find #"[\\\.\w]+png" %) (s/split-lines (:err result)))]
    (map f/getAbsolutePath errors)))

(comment
  (let [raw-result (shell/sh "cmd" "/c" "npx  marp E://Documents/descript-to-video/sample/sample.md --images png --allow-local-files --image-scale 1.5")]
    (println raw-result))
  (def result (export-slides "E://Documents/descript-to-video/sample/sample.md"))
  (def infos (s/split-lines (:err result)))
  (str (second infos))
  (def output-png-pathes (filter (complement nil?) (map #(re-find #"[\\\.\w]+png" %) infos)))
  (f/getAbsolutePath (first output-png-pathes))
  (map f/getAbsolutePath '())
  (cond->)
  (println (:err result))
  )