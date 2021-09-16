(ns descript-to-video.aviutl.parser
  (:require
   [clojure.string :as s]
   [yaml.core :as yaml]
   [clojure.walk :as w]))

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

(defn format-key [symbol]
  (str "[" (name symbol) "]"))

(defn format-level [key val]
  (str (name key) "=" val))

(defn get-nested-keys
  "ネストしたmapのkeyをvectorとして返す"
  [mp result]
  (loop [todos (map list (keys mp))
         temp result]
    (let [first-keys (first todos)
          val (get-in mp  first-keys)]
      (cond
        (empty?  todos) temp
        (map? val) (recur
                    (concat (map #(flatten (list first-keys %)) (keys val)) (rest todos))
                    (conj temp first-keys))
        :else (recur
               (rest todos)
               (conj temp first-keys))))))

(defn yaml->aviutl-object
  [mp]
  (letfn [
          (format-keyval
           [mp keys]
           (let [val (get-in mp keys)]
             (cond (map? val) (format-key (last keys))
                   :else (format-level (last keys) val))))]
    (s/join "\r\n" (reduce #(conj %1 (format-keyval mp %2)) '[] (get-nested-keys mp '[])))))

(comment
  ;; 動作確認に使っているスニペット系
  ;; TODO:テストに移す
  (def raw-object (slurp "./sample/sample.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))
  (def parsed  (aviutl-obj->yaml raw-object))
  (get-nested-keys (:0.1 (:0 sample-yaml)) '())
  (get-nested-keys (:1 sample-yaml) '[])
  (get-nested-keys sample-yaml '[])
  (spit "../tmp.txt" (yaml->aviutl-object sample-yaml) :encoding "shift-jis")
  (conj '(:0 :0.0) :name)
  
  (map list (keys parsed))
  (map #(list :exedit %) (keys (get-in parsed '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in parsed '(:exedit)))))
  (reverse (cons (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (reverse (concat (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (keys sample-yaml)

  )