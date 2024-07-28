package tests

import (
	"testing"

	"github.com/elc49/vuno/Server/src/util"
	"github.com/stretchr/testify/assert"
)

func TestUtil(t *testing.T) {
	t.Run("string_randomness", func(t *testing.T) {
		s := util.RandomStringByLength(5)
		assert.Equal(t, len(s), 6, "string should equal to 6 in length")
	})
}
