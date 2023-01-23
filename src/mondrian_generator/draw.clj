(ns mondrian-generator.draw
  (:require [mondrian-generator.core :as core]
            [quil.core :as q]))


(def rgb {:red [255 0 0]
          :blue [66 135 245]
          :yellow [252 186 3]
          :black [0]
          :white [255]})

(defn draw-section [section]
  (let [{:keys [top left right bottom color]} section
        width (- right left)
        height (- bottom top)]
    (apply q/fill (rgb color))
    (q/rect left top width height)))

(defn draw []
  ; white background
  (q/no-loop)
  (q/stroke-weight 5)
  (q/background 255)
  (doseq [section
          (:sections
           (core/generate-mondrian
            {:max-x 1500 :max-y 1000}
            (fn [section] (core/division-generator section 2))
            ;; TODO: color generator will work in function of the contiguos section
            ;; prefer some color combinations over others
            (fn [] (core/random-color
                 ;; TODO: random colors given weight
                    [:red :blue :yellow :white :white :white :blue :yellow :white :white]))
            4))]
    (draw-section section))
  (q/save "generated/image.png")
  ;; comment to show window with output
  (q/exit))


(q/defsketch example
  :host "host"
  :title "Mondrianesque"
  ;; TODO: config map
  :size [1500 1000]
  :draw draw)
