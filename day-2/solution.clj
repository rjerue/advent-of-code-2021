#!/usr/bin/env bb
(require '[clojure.string :as str])

(defn
  position-reducer
  [{:keys [x y] :as position} [direction value-string]]
  (let [value (Integer/parseInt value-string)]
    (case direction
      "forward" (assoc position :x (+ x value))
      "down" (assoc position :y (+ y value))
      "up" (assoc position :y (- y value)))))

(defn
  position-reducer-aim
  [{:keys [x y aim] :as position} [direction value-string]]
  (let [value (Integer/parseInt value-string)]
    (case direction
      "forward" (assoc position :x (+ x value) :y (+ y (* aim value)))
      "down" (assoc position :aim (+ aim value))
      "up" (assoc position :aim (- aim value)))))

(def base
  (->> *command-line-args*
       (first)
       (slurp)
       (str/split-lines)
       (map #(str/split % #" "))))

(defn multiply-xy
  [xy-map]
  (apply * (vals (select-keys xy-map [:x :y]))))

(println "Part 1"
         (->> base
          (reduce position-reducer {:x 0 :y 00})
          (multiply-xy)))

(println "Part 2"
         (->> base
              (reduce position-reducer-aim {:x 0 :y 0 :aim 0})
              (multiply-xy)))
