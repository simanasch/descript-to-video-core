(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]))

(defn indent
  "aviutlのobject形式のstringをyamlでparseできるようインデントする"
  [string]
  (loop [lines (s/split-lines string)
         nest ""
         result '[]]
    (if
     (nil? (first lines)) (s/join "\r\n" result)
     (let [line (first lines)
           match (re-matches #"\[\w+\]" line)]
       (cond
        ;;  [exedit]か[{レイヤー番号}]を含む行の場合
         (not= nil match) (recur (rest lines) "  " (conj result line))
        ;;  オプション定義の行の場合
         (s/includes? line "[") (recur (rest lines) "    " (conj result (str nest line)))
         ;; それ以外の場合、nestの値そのままでもう一度コール
         :else (recur (rest lines) nest (conj result (str nest line))))))))

(defn aviutl-obj->yaml
  "aviutlのobjectをyaml形式でparseする"
  [text]
  (->
   text
   (indent)
   (s/replace "[" "\"")
   (s/replace "]" "\": ")
   (s/replace "=" ": ")
   (yaml/parse-string)))

;; (defn yaml->aviutl-object
;;   [yamlMap]
;;   (loop [element yamlMap]
;;     (if (map? element) )
;;     ))

(comment

  (def raw-object (slurp "./sample/sample.exo" :encoding "shift-jis"))
  (def parsed  (aviutl-obj->yaml raw-object))
  (:1.0 (:1 parsed))
  (into '[] parsed)
  (map #(s/trim %) parsed)
  (indent raw-object)
  (aviutl-obj->yaml raw-object)
  (reduce #(str %1 %2) parsed)
  )