import React, { useState, useEffect } from "react"
import { Link } from "react-router-dom"
import { getProducts, getVariants, getBrands } from "@/lib/api"
import { getMinPrice, isOutOfStock } from "@/lib/utils"
import { ProductCard } from "@/components/ProductCard"

export default function BestSellers() {
  const [products, setProducts] = useState([])
  const [variants, setVariants] = useState([])
  const [brands, setBrands] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [productsData, variantsData, brandsData] = await Promise.all([
          getProducts(),
          getVariants(),
          getBrands()
        ])
        
        // Filter active products and take first 6
        const activeProducts = productsData.filter(p => p.status === 1).slice(0, 6)
        setProducts(activeProducts)
        setVariants(variantsData)
        setBrands(brandsData)
      } catch (error) {
        console.error("Failed to fetch data:", error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  if (loading) {
    return (
      <section className="section">
        <h2 className="text-2xl font-semibold tracking-tight text-foreground mb-4">Bán chạy</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(6)].map((_, i) => (
            <div key={i} className="card animate-pulse">
              <div className="aspect-square bg-muted rounded-lg mb-4"></div>
              <div className="h-4 bg-muted rounded mb-2"></div>
              <div className="h-4 bg-muted rounded w-2/3"></div>
            </div>
          ))}
        </div>
      </section>
    )
  }

  return (
    <section className="section">
      <div className="flex items-baseline justify-between mb-4">
        <h2 className="text-2xl font-semibold tracking-tight text-foreground">Bán chạy</h2>
        <Link to="/products" className="link-underline text-sm">Xem tất cả</Link>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {products.map((product) => {
          const productVariants = variants.filter((v) => v.productId === product.id && v.status === 1)
          const brand = brands.find((b) => b.id === product.brandId)
          const firstVariant = productVariants[0]
          
          // Images is a direct public CDN URL (Cloudflare R2)
          let imageUrl = "/placeholder.svg?height=400&width=400"
          if (firstVariant?.images) {
            imageUrl = firstVariant.images
          }

          return (
            <ProductCard
              key={product.id}
              product={product}
              brand={brand?.name || "Không xác định"}
              image={imageUrl}
              minPrice={getMinPrice(productVariants)}
              rating={0}
              reviewCount={0}
              isOutOfStock={isOutOfStock(productVariants)}
            />
          )
        })}
      </div>
    </section>
  )
}
