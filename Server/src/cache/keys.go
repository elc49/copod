package cache

import "fmt"

func IpCacheKey(ip string) string {
	return fmt.Sprintf("ip:%s", ip)
}
