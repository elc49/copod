package util

import (
	"image"
	"image/color"
	"math/rand"
)

func RandomAvatar(w, h int) image.Image {
	img := image.NewRGBA(image.Rect(0, 0, w, h))
	for x := 0; x < w; x += 25 {
		for y := 0; y < h; y += 25 {
			r, g, b := randomColor()
			drawRectangleImage(img, x, y, x+25, y+25, color.RGBA{r, g, b, 255})
		}
	}

	return img
}

func drawRectangleImage(img *image.RGBA, x1, y1, x2, y2 int, c color.Color) {
	for x := x1; x < x2; x++ {
		for y := y1; y < y2; y++ {
			img.Set(x, y, c)
		}
	}
}

func randomColor() (uint8, uint8, uint8) {
	return uint8(rand.Intn(256)), uint8(rand.Intn(256)), uint8(rand.Intn(256))
}
