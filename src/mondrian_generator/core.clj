(ns mondrian-generator.core
  (:gen-class))

(defn random-color [options]  (rand-nth options))

(defn random-division [options] (rand-nth options))

(defn is-horizontal-division [] (rand-nth [true false]))

(defn random-max-level [options] (rand-nth options))

(defn create-section [top, left, right, bottom, color, level]
  {:top top
   :left left
   :right right
   :bottom bottom
   :color color
   :level level})

(defn create-horizontal-subsections [section division colors]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ left (* division (- right left)))]
    [(create-section
      top left line bottom (random-color colors) new-level)
     (create-section
      top line right bottom (random-color colors) new-level)]))

(defn create-vertical-subsections [section division colors]
  (let [{:keys [top bottom left right level]} section
        new-level (inc level)
        line  (+ left (* division (- right left)))]
    [(create-section top left line bottom (random-color colors) new-level)
     (create-section top line right bottom (random-color colors) new-level)]))


(defn split-section [section division colors]
  (if (is-horizontal-division)
    (create-horizontal-subsections section division colors)
    (create-vertical-subsections section division colors)))