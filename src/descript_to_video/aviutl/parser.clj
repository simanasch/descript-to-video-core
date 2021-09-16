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

(defn format-map
  [mp result]
  (loop [todos (map list (keys mp))
         temp result]
    ;; (println (first todos))
    ;; (println (get-in mp  (first todos)))
    ;; (println (empty? todos))
    (let [first-keys (first todos)
          val (get-in mp  first-keys)]
      (cond
        (empty?  todos) temp
        (map? val) (recur
                    (concat (map #(reverse (conj first-keys %)) (keys val)) (rest todos))
                    (conj temp (format-key (last first-keys))))
        :else (recur
               (rest todos)
               (conj temp (format-level (last first-keys) val)))))))

(defn get-nested-keys
  "ネストしたmapのkeyをvectorとして返す"
  [mp result]
  (loop [todos (map list (keys mp))
         temp result]
    ;; (println (first todos))
    ;; (println (get-in mp  (first todos)))
    ;; (println (empty? todos))
    (let [first-keys (first todos)
          val (get-in mp  first-keys)]
      (cond
        (empty?  todos) temp
        (map? val) (recur
                    (concat (map #(reverse (conj first-keys %)) (keys val)) (rest todos))
                    (conj temp first-keys))
        :else (recur
               (rest todos)
               (conj temp first-keys))))))

(comment
  ;; 動作確認に使っているスニペット系
  ;; TODO:テストに移す
  (def raw-object (slurp "./sample/sample.exo" :encoding "shift-jis"))
  (def sample-yaml (yaml/from-file "./sample/sample.yaml"))
  (def parsed  (aviutl-obj->yaml raw-object))
  (yaml->aviutl-object sample-yaml)
  (map? (:0.1 (:0 sample-yaml)))
  (map? (:X (:0.1 (:0 sample-yaml))))
  (format-map (:0.1 (:0 sample-yaml)) '[])
  (format-map (:1 sample-yaml) '[])
  (format-map sample-yaml '[])
  (get-nested-keys (:0.1 (:0 sample-yaml)) '())
  (get-nested-keys (:1 sample-yaml) '[])
  (map list (keys parsed))
  (map #(list :exedit %) (keys (get-in parsed '(:exedit))))
  (map #(get-in sample-yaml %) (map #(list :exedit %) (keys (get-in parsed '(:exedit)))))
  (get-in sample-yaml '(:0 :0.0 :サイズ))
  (reverse (cons (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (reverse (concat (first (keys (get-in sample-yaml '(:0 :0.1)))) '(:0.1)))
  (empty? '())
  (def trs '((:1.0) (:1.1) (:1.2) (:1.3)))
  (def nests '((:1.0 :_name) (:1.0 :サイズ) (:1.0 :表示速度) (:1.0 :文字毎に個別オブジェクト) (:1.0 :移動座標上に表示する) (:1.0 :自動スクロール) (:1.0 :B) (:1.0 :I) (:1.0 :type) (:1.0 :autoadjust) (:1.0 :soft) (:1.0 :monospace) (:1.0 :align) (:1.0 :spacing_x) (:1.0 :spacing_y) (:1.0 :precision) (:1.0 :color) (:1.0 :color2) (:1.0 :font) (:1.0 :text)))
  (conj trs nests)
  (cons trs nests)
  (concat nests trs)
  (keys sample-yaml)
  

  (rest (:1 parsed))
  (def nodes '('(0 1 4 2)
               '(1 0 3)
               '(2 0 5)
               '(3 1 7 8)
               '(4 0 8)))

  )