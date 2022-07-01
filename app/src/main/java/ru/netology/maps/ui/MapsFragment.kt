package ru.netology.maps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider
import ru.netology.maps.R
import ru.netology.maps.databinding.FragmentMapsBinding
import ru.netology.maps.dto.Place
import ru.netology.maps.initialization.MapKitInitializer
import ru.netology.maps.viewmodel.PlaceViewModel


class MapsFragment : Fragment(), LocationListener, InputListener {

    private val viewModel: PlaceViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val bundle = Bundle()
    private val apiKey = "SET_API_KEY_HERE"
    private var position: Point? = null
    private lateinit var yandexMap: MapView
    private lateinit var userLocation: UserLocationLayer
    private lateinit var marksObject: MapObjectCollection
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                yandexMap.apply {
                    userLocation.isVisible = true
                    userLocation.isHeadingEnabled = false
                }
            } else {
                Toast.makeText(
                    context,
                    "Sorry, but the app won't work without geolocation permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitInitializer.initialize(apiKey, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapsBinding.inflate(layoutInflater)

        binding.apply {
            navigation.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.all -> {
                        findNavController().navigate(R.id.action_mapsFragment_to_placeFragment)
                    }
                    R.id.favorites -> {
                        findNavController().navigate(R.id.action_mapsFragment_to_placeFragment)
                    }
                    R.id.recent -> {
                        findNavController().navigate(R.id.action_mapsFragment_to_placeFragment)
                    }
                    R.id.settings -> {
                        findNavController().navigate(R.id.action_mapsFragment_to_mySettingsFragment)
                    }
                    R.id.info -> {
                        findNavController().navigate(R.id.action_mapsFragment_to_informationFragment)
                    }
                }
                drawer.closeDrawer(GravityCompat.START)
                true
            }

            nearme.setOnClickListener {
                userLocation.cameraPosition()?.let { moveCamera(it.target) }
            }

            zoomUp.setOnClickListener {
                yandexMap.map.move(
                    CameraPosition(
                        yandexMap.map.cameraPosition.target,
                        yandexMap.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                    ),
                    Animation(Animation.Type.SMOOTH, 1F),
                    null
                )
            }

            zoomDown.setOnClickListener {
                yandexMap.map.move(
                    CameraPosition(
                        yandexMap.map.cameraPosition.target,
                        yandexMap.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                    ),
                    Animation(Animation.Type.SMOOTH, 1F),
                    null
                )
            }
        }

        yandexMap = binding.map
        userLocation = MapKitFactory.getInstance().createUserLocationLayer(yandexMap.mapWindow)
        marksObject = yandexMap.map.mapObjects.addCollection()
        locationManager = MapKitFactory.getInstance().createLocationManager()
        yandexMap.map.addInputListener(this)

        viewModel.data.observe(viewLifecycleOwner) { PlaceModel ->
            PlaceModel.places.map {
                addMarker(Point(it.latitude, it.longitude))
            }
        }

        userLocation.apply {
            setAnchor(
                PointF((yandexMap.width * 0.5f), (yandexMap.height * 0.5f)),
                PointF((yandexMap.width * 0.5f), (yandexMap.height * 0.83f))
            )
            resetAnchor()
        }

        viewModel.data.observe(viewLifecycleOwner){ placeModel ->
            placeModel.places.map {

            }

        }

        position = Point(
            arguments?.getDouble("lat") ?: 55.75222,
            arguments?.getDouble("lng") ?: 37.61556
        )

        checkPermission()
        moveCamera(position!!)
        return binding.root
    }

    private fun moveCamera(point: Point) {
        yandexMap.map.move(CameraPosition(point, 16F, 0F, 0F))
    }

    private fun addMarker(point: Point) {
        val marker = View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_location_on_24dp)
        }
        yandexMap.map.mapObjects.addPlacemark(
            point,
            ViewProvider(marker)
        )
    }

    override fun onMapTap(p0: Map, p1: Point) {
        yandexMap.map.deselectGeoObject()
    }

    override fun onMapLongTap(p0: Map, point: Point) {
        bundle.apply {
            putDouble("latitude", point.latitude)
            putDouble("longitude", point.longitude)
        }
        viewModel.edit(Place.empty)
        findNavController().navigate(R.id.action_mapsFragment_to_newPlaceFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        yandexMap.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        yandexMap.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun checkPermission() = lifecycle.coroutineScope.launchWhenCreated {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                yandexMap.apply {
                    userLocation.isVisible = true
                    userLocation.isHeadingEnabled = false
                }

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity())

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    println(it)
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // TODO: show rationale dialog
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onLocationUpdated(p0: Location) {
        TODO("Not yet implemented")
    }

    override fun onLocationStatusUpdated(p0: LocationStatus) {
        TODO("Not yet implemented")
    }
}



