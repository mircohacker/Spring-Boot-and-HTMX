package org.example.ssrdemo;

import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView


@Controller
class ProductsController {

    val products = listOf(Product(id = "one"), Product(desc = "The detail page for this product will only work when the requests lands on the same lamda."))

    @GetMapping("/products")
    fun getProducts(): ModelAndView {
        return ModelAndView("sites/products", mapOf("products" to products))
    }

    @GetMapping("/products", headers = ["HX-Request=true"])
    fun getProductsHtmx(): ModelAndView {
        return ModelAndView("fragments/products", mapOf("products" to products))
    }

    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: String): ModelAndView {
        return ModelAndView("sites/product", mapOf("product" to getProductById(id)))
    }

    @GetMapping("/products/{id}", headers = ["HX-Request=true"])
    fun getProductHtmx(@PathVariable id: String): ModelAndView {
        Thread.sleep(1000)
        return ModelAndView("fragments/product", mapOf("product" to getProductById(id)))
    }

    private fun getProductById(id: String): Product? {
        val product = products.findLast { product -> product.id == id }
        if(product === null){
            throw NotFoundException("The requested product was not found")
        }
        return product
    }
}
