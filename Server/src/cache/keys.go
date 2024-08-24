package cache

import "fmt"

func IpCacheKey(ip string) string {
	return fmt.Sprintf("ip:%s", ip)
}

func UserCacheKey(id string) string {
	return fmt.Sprintf("user:%s", id)
}
