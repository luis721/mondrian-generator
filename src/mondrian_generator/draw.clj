(ns mondrian-generator.draw
  (:require [quil.core :as q]
            [mondrian-generator.core :as core]))


(def rgb {:red [255 0 0]
          :blue [66 135 245]
          :yellow [209 179 27]
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
  (run! draw-section
        (core/generate-mondrian
         {:max-x 1500 :max-y 1000}
         [0 0.3 0.5 0.5 0.5 0.4 1]
         ;; TODO: color generator will work in function of the contiguos section
         ;; prefer some color combinations over others
         (fn [] (core/random-color
                 ;; TODO: random colors given weight
                 [:red :blue :yellow :white :white :white :blue :yellow :white :white]))
         4))
  (q/save "generated/image.png")
  ;; comment to show window with output
  (q/exit))


(q/defsketch example
  :host "host"
  :title "Mondrianesque"
  ;; TODO: config map
  :size [1500 1000]
  :draw draw)
