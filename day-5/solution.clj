#!/usr/bin/env bb
(require '[clojure.string :as str])

(defn pair-str->pair-num
  [args]
  (mapv #(Integer/parseInt %) args))

(defn ->coords
  [row]
  (->> (str/split row #" -> ")
       (map #(str/split % #","))
       (mapv pair-str->pair-num)))

(def base
  (->> (or *command-line-args* ["example.txt"])
       (first)
       (slurp)
       (str/split-lines)
       (map ->coords)))

(defn make-points
  [coords func min max]
  (reduce #(conj %1 (func %2)) 
          (set coords) 
          (apply range (sort [min max]))))

(defn diag-point-maker
  [coords check-y modify-y]
  (let [sorted (sort-by first coords)
        [start end] sorted
        [x1 y1] start
        [x2] end]
    (loop [x x1
           y y1
           coords (set coords)]
      (let [target [x y]]
        (cond (or (check-y y)
                  (> x x2)) nil
              (= target end) coords
              :else (recur (inc x) (modify-y y) (conj coords target)))))))

(defn make-diag-points
  [coords]
  (let [sorted (sort-by first coords)
        [_start end] sorted
        [_x2 y2] end]
    (or
     (diag-point-maker coords #(> % y2) inc)
     (diag-point-maker coords #(< % y2) dec)
     [])))

(defn ocean
  [diag?]
  (reduce (fn [universe coords]
            (let [[[x1 y1] [x2 y2]] coords
                  new
                  (cond
                    (= y1 y2) (make-points coords #(vector % y1) x1 x2)
                    (= x1 x2) (make-points coords #(vector x1 %) y1 y2)
                    :else (if diag? (make-diag-points coords) []))]
              (->> new
                   (reduce #(into %1 (hash-map %2 1)) {})
                   (merge-with + universe))))
          {}
          base))

(->> (ocean false)
     (vals)
     (filter #(< 1 %))
     (count)
     (println "Part 1"))

(->> (ocean true)
     (vals)
     (filter #(< 1 %))
     (count)
     (println "Part 2"))
