package com.example.kleine.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kleine.R
import com.example.kleine.SpacingDecorator.HorizantalSpacingItemDecorator
import com.example.kleine.activities.ShoppingActivity
import com.example.kleine.adapters.recyclerview.ColorsAndSizesAdapter
import com.example.kleine.adapters.viewpager.ViewPager2Images
import com.example.kleine.databinding.FragmentProductPreviewBinding
import com.example.kleine.model.CartProduct
import com.example.kleine.model.Product
import com.example.kleine.resource.Resource
import com.example.kleine.util.Constants.Companion.COLORS_TYPE
import com.example.kleine.util.Constants.Companion.SIZES_TYPE
import com.example.kleine.viewmodel.shopping.ShoppingViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.vejei.viewpagerindicator.indicator.CircleIndicator

class ProductPreviewFragment : Fragment() {

    val args by navArgs<ProductPreviewFragmentArgs>()
    val TAG = "ProductPreviewFragment"

    private lateinit var binding: FragmentProductPreviewBinding
    private lateinit var colorsAdapter: ColorsAndSizesAdapter
    private lateinit var sizesAdapter: ColorsAndSizesAdapter
    private lateinit var viewPagerAdapter: ViewPager2Images
    private lateinit var viewModel: ShoppingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorsAdapter = ColorsAndSizesAdapter(COLORS_TYPE)
        sizesAdapter = ColorsAndSizesAdapter(SIZES_TYPE)
        viewPagerAdapter = ViewPager2Images()
        viewModel = (activity as ShoppingActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProductPreviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE

        val product = args.product

        setupViewpager()
        setupColorsRecyclerview()
        setupSizesRecyclerview()


        setProductInformation(product)

        onImageCloseClick()
        onBtnAddToCartClick()

        observeAddToCart()

        onColorClick()
        onSizeClick()
    }

    private var selectedSize: String = ""
    private fun onSizeClick() {
        sizesAdapter.onItemClick = { size ->
            selectedSize = size
            binding.tvSizeError.visibility = View.INVISIBLE

        }
    }

    private var selectedColor: String = ""
    private fun onColorClick() {
        colorsAdapter.onItemClick = { color ->
            selectedColor = color
            binding.tvColorError.visibility = View.INVISIBLE
        }
    }


    private fun observeAddToCart() {
        viewModel.addToCart.observe(viewLifecycleOwner, Observer { response ->
            val btn = binding.btnAddToCart
            when (response) {
                is Resource.Loading -> {
                    startLoading()
                    return@Observer
                }

                is Resource.Success -> {
                    stopLoading()
                    viewModel.addToCart.value = null
                    return@Observer
                }

                is Resource.Error -> {
                    Toast.makeText(activity, "Oops! error occurred", Toast.LENGTH_SHORT).show()
                    viewModel.addToCart.value = null
                    Log.e(TAG, response.message.toString())
                }
            }
        })
    }

    private fun stopLoading() {
        binding.apply {
            btnAddToCart.visibility = View.VISIBLE
            progressbar.visibility = View.INVISIBLE
        }
    }

    private fun startLoading() {
        binding.apply {
            btnAddToCart.visibility = View.INVISIBLE
            progressbar.visibility = View.VISIBLE
        }
    }


    private fun onBtnAddToCartClick() {
        binding.btnAddToCart.apply {
            setOnClickListener {

                if (selectedColor.isEmpty()) {
                    binding.tvColorError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (selectedSize.isEmpty()) {
                    binding.tvSizeError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                val product = args.product
               // val image = (product.images?.get(IMAGES) as List<String>)[0]
                val image = (product.images?.get(0) as List<String>)[0]
                val cartProduct = CartProduct(
                    id= product.id!!,
                    name = product.title!!,
                    store="",
                    image=image,
                    price=product.price!!,
              //      product.newPrice,
                    newPrice = null,
                    quantity=1,
                    color=selectedColor,
                    size=selectedSize
                )
                viewModel.addProductToCart(cartProduct)
                setBackgroundResource(R.color.g_black)
            }
        }
    }

    private fun onImageCloseClick() {
        binding.imgClose.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProductInformation(product: Product) {
        val imagesList = product.images
        val colors = product.colors?.map { '#'+Integer.toHexString(it) }
    //    val colors = emptyList<String>()
        //    val colors = product.colors!![0] as List<String>
        val size = product.sizes!![0]
        val sizes = product.sizes
        binding.apply {
            viewPagerAdapter.differ.submitList(imagesList)
            colors?.let {
                if (it.isNotEmpty())
                    colorsAdapter.differ.submitList(it)
            }
//            if (colors != null && colors.isNotEmpty())
//                colorsAdapter.differ.submitList(colors)
            if (sizes.isNotEmpty() && sizes[0] != "")
                sizesAdapter.differ.submitList(sizes)
            tvProductName.text = product.title
            tvProductDescription.text = product.description
            tvProductPrice.text = "€${product.price}"
            tvProductOfferPrice.visibility = View.GONE
//            product.newPrice?.let {
//                if (product.newPrice.isNotEmpty() && product.newPrice != "0") {
//                    tvProductPrice.paintFlags =
//                        tvProductPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                    tvProductOfferPrice.text = "$${product.newPrice}"
//                    tvProductOfferPrice.visibility = View.VISIBLE
//                }
//            }
            product.sizeUnit?.let {
                if (it.isNotEmpty()) {
                    binding.tvSizeUnit.visibility = View.VISIBLE
                    binding.tvSizeUnit.text = " ($it)"
                }
            }
        }
    }

    private fun setupSizesRecyclerview() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizantalSpacingItemDecorator(45))
        }
    }

    private fun setupColorsRecyclerview() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizantalSpacingItemDecorator(45))
        }
    }

    private fun setupViewpager() {
        binding.viewpager2Images.adapter = viewPagerAdapter
        binding.circleIndicator.setWithViewPager2(binding.viewpager2Images)
//        binding.circleIndicator.itemCount = (args.product.images?.get(IMAGES) as List<String>).size
        //binding.circleIndicator.itemCount = (args.product.images?.get(0) as List<String>).size
        binding.circleIndicator.itemCount = args.product.images.size
        binding.circleIndicator.setAnimationMode(CircleIndicator.AnimationMode.SLIDE)
    }

}