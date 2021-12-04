#!/usr/bin/env bb
(require '[clojure.string :as str])

(def base
  (->> *command-line-args*
       (first)
       (slurp)
       (str/split-lines)))

(defn calc
  [{:keys [g e] :as values} kv]
  (if (> (get kv \0) (get kv \1))
    (assoc values :g (str g "0") :e (str e "1"))
    (assoc values :g (str g "1") :e (str e "0"))))

(defn binary-str->number
  [coll]
  (reduce (fn [sum [index current]]
            (+ sum (* (Character/getNumericValue current) (int (Math/pow 2 index))))) 
          0 
          (map-indexed vector (reverse coll))))

(defn calc-frequencies
  [base]
  (->>
   (range 0 (count (first base)))
   (map (fn [index] (frequencies (map #(nth % index) base))))))

(->> base
     (calc-frequencies)
     (reduce calc {:g "" :e ""})
     (vals)
     (map binary-str->number)
     (apply *)
     (println "Part 1"))

(defn digit-common
  [less greater]
  (fn [hmap]
    (let [zero (get hmap \0 0)
          one (get hmap \1 0)]
      (if (> zero one)
        less
        greater))))

(defn life-support [decider]
  (loop [n 0
         list base]
    (if (= 1 (count list))
      list
      (let [popular (nth (->> list (calc-frequencies) (map decider)) n)]
        (recur (+ n 1) (filter #(= popular (str (nth % n))) list))))))

(->> [(life-support (digit-common "0" "1")) (life-support (digit-common "1" "0"))]
     (map first)
     (map binary-str->number)
     (apply *)
     (println "Part 2"))
