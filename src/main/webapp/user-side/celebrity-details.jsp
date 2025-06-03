<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Celebrity, model.Movie, model.Sport, model.Music, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Celebrity Details - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { 
            font-family: 'Poppins', sans-serif;
            background-color: #f5f7fa;
        }
        .image-gallery img { 
            transition: transform 0.3s ease; 
        }
        .image-gallery img:hover { 
            transform: scale(1.05); 
        }
        .thumbnail-active {
            border: 2px solid #F4A300;
            opacity: 1;
        }
        .thumbnail {
            opacity: 0.7;
            cursor: pointer;
        }
        .thumbnail:hover {
            opacity: 1;
        }
        .item-image { 
            width: 100%; 
            height: 150px; 
            object-fit: cover; 
            border-radius: 8px; 
        }
        .section-box { 
            background-color: #f9fafb; 
            border-left: 4px solid #F4A300; 
            padding-left: 1rem; 
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="container mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <a href="${pageContext.request.contextPath}/index" class="text-2xl font-bold text-[#F4A300]">Nepal Navigator</a>
                <div class="hidden md:flex space-x-6">
                    <a href="${pageContext.request.contextPath}/index" class="hover:text-[#F4A300]">Home</a>
                    <a href="${pageContext.request.contextPath}/foods" class="hover:text-[#F4A300]">Foods</a>
                    <a href="${pageContext.request.contextPath}/attractions" class="hover:text-[#F4A300]">Attractions</a>
                    <a href="${pageContext.request.contextPath}/music" class="hover:text-[#F4A300]">Music</a>
                    <a href="${pageContext.request.contextPath}/movies" class="hover:text-[#F4A300]">Movies</a>
                    <a href="${pageContext.request.contextPath}/sports" class="hover:text-[#F4A300]">Sports</a>
                    <% String username = (String) session.getAttribute("username");
                       if (username != null) { %>
                        <span class="text-white">Welcome, <%= username %>!</span>
                        <a href="${pageContext.request.contextPath}/logout" class="hover:text-[#F4A300]">Logout</a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/login" class="hover:text-[#F4A300]">Login/Register</a>
                    <% } %>
                </div>
                <button class="md:hidden">
                    <i class="fas fa-bars text-2xl"></i>
                </button>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 py-8">
        <% Celebrity celebrity = (Celebrity) request.getAttribute("celebrity");
           if (celebrity == null) {
               response.sendRedirect(request.getContextPath() + "/index");
               return;
           }
        %>
        <!-- Breadcrumb -->
        <div class="mb-6">
            <nav class="flex" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-1 md:space-x-3">
                    <li class="inline-flex items-center">
                        <a href="${pageContext.request.contextPath}/index" class="text-gray-600 hover:text-[#F4A300]">Home</a>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <span class="text-gray-500"><%= celebrity.getName() != null ? celebrity.getName() : "Unknown" %></span>
                        </div>
                    </li>
                </ol>
            </nav>
        </div>

        <!-- Celebrity Details -->
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <!-- Image Gallery -->
            <div class="p-8 image-gallery">
                <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
                    <div class="relative h-96 rounded-lg overflow-hidden md:col-span-3">
                        <img id="main-image" 
                             src="${pageContext.request.contextPath}<%= celebrity.getImage() != null ? celebrity.getImage() : "/images/placeholder.jpg" %>" 
                             alt="<%= celebrity.getName() != null ? celebrity.getName() : "Celebrity Image" %>" 
                             class="w-full h-full object-cover"
                             onerror="this.src='https://via.placeholder.com/800'">
                        <div class="absolute top-4 right-4">
                            <button class="bg-white p-2 rounded-full shadow-lg hover:bg-gray-100">
                                <i class="fas fa-heart text-red-500"></i>
                            </button>
                        </div>
                    </div>
                    <div class="grid grid-cols-4 gap-4 md:col-span-2">
                        <% for (int i = 0; i < 4; i++) { %>
                            <div class="h-24 rounded-lg overflow-hidden">
                                <img src="${pageContext.request.contextPath}<%= celebrity.getImage() != null ? celebrity.getImage() : "/images/placeholder.jpg" %>" 
                                     alt="<%= celebrity.getName() != null ? celebrity.getName() : "Celebrity Image" %>" 
                                     class="w-full h-full object-cover cursor-pointer thumbnail <%= i == 0 ? "thumbnail-active" : "" %>"
                                     onclick="changeMainImage(this.src)"
                                     onerror="this.src='https://via.placeholder.com/200'">
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- Celebrity Information -->
            <div class="p-8">
                <div class="space-y-6">
                    <div>
                        <h1 class="text-3xl font-bold text-[#002B5B]"><%= celebrity.getName() != null ? celebrity.getName() : "Unknown Celebrity" %></h1>
                        <div class="flex items-center mt-2">
                            <div class="flex text-yellow-400">
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star-half-alt"></i>
                            </div>
                            <span class="text-gray-600 ml-2">4.5 (50 reviews)</span>
                        </div>
                    </div>
                    <div>
                        <h2 class="text-xl font-semibold text-[#002B5B]">Biography</h2>
                        <p class="text-gray-600 mt-2"><%= celebrity.getBio() != null ? celebrity.getBio() : "No biography available." %></p>
                    </div>
                </div>
            </div>

            <!-- Associated Movies -->
			<div class="border-t border-gray-200 p-8 section-box">
			    <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Movies</h2>
			    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
			        <% List<Movie> movies = (List<Movie>) request.getAttribute("movies");
			           if (movies != null && !movies.isEmpty()) {
			               for (Movie movie : movies) { %>
			        <div class="bg-white rounded-lg shadow overflow-hidden">
			            <img src="${pageContext.request.contextPath}<%= movie.getImage() != null ? movie.getImage() : "/images/placeholder.jpg" %>" 
			                 alt="<%= movie.getTitle() != null ? movie.getTitle() : "Movie Image" %>" 
			                 class="item-image" 
			                 onerror="this.src='https://via.placeholder.com/500'">
			            <div class="p-4">
			                <h3 class="text-lg font-semibold text-[#002B5B] mb-2"><%= movie.getTitle() != null ? movie.getTitle() : "Unknown" %></h3>
			                <p class="text-gray-600 text-sm mb-2 line-clamp-2"><%= movie.getDescription() != null ? movie.getDescription() : "No description available." %></p>
			                <div class="flex items-center text-sm text-gray-500">
			                    <div class="flex text-yellow-400 mr-2">
			                        <% float rating = movie.getRating();
			                           int fullStars = (int) rating;
			                           boolean hasHalfStar = rating - fullStars >= 0.5;
			                           for (int i = 0; i < fullStars; i++) { %>
			                               <i class="fas fa-star"></i>
			                           <% }
			                              if (hasHalfStar) { %>
			                               <i class="fas fa-star-half-alt"></i>
			                           <% }
			                              for (int i = fullStars + (hasHalfStar ? 1 : 0); i < 5; i++) { %>
			                               <i class="far fa-star"></i>
			                           <% } %>
			                    </div>
			                    <span><%= movie.getRating() %>/5</span>
			                </div>
			                <div class="mt-4">
			                    <a href="${pageContext.request.contextPath}/movie-detail?id=<%= movie.getId() %>" 
			                       class="text-[#F4A300] hover:text-[#A31621] text-sm">
			                        View Details →
			                    </a>
			                </div>
			            </div>
			        </div>
			        <%      }
			           } else { %>
			            <p class="text-gray-600">No associated movies found.</p>
			        <% } %>
			    </div>
			</div>

            <!-- Associated Sports -->
            <div class="border-t border-gray-200 p-8 section-box">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Sports</h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                    <% List<Sport> sports = (List<Sport>) request.getAttribute("sports");
                       if (sports != null && !sports.isEmpty()) {
                           for (Sport sport : sports) { %>
                    <div class="bg-white rounded-lg shadow overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= sport.getImage() != null ? sport.getImage() : "/images/placeholder.jpg" %>" 
                             alt="<%= sport.getName() != null ? sport.getName() : "Sport Image" %>" 
                             class="item-image" 
                             onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-4">
                            <h3 class="text-lg font-semibold text-[#002B5B] mb-2"><%= sport.getName() != null ? sport.getName() : "Unknown Sport" %></h3>
                            <p class="text-gray-600 text-sm mb-2 line-clamp-2"><%= sport.getDescription() != null ? sport.getDescription() : "No description available." %></p>
                            <p class="text-sm text-gray-500 mb-2"><i class="fas fa-trophy mr-2"></i><%= sport.getStatus() != null ? sport.getStatus() : "Unknown Status" %></p>
                            <div class="mt-4">
                                <a href="${pageContext.request.contextPath}/sport-detail?id=<%= sport.getId() %>" 
                                   class="text-[#F4A300] hover:text-[#A31621] text-sm">
                                    View Details →
                                </a>
                            </div>
                        </div>
                    </div>
                    <%      }
                       } else { %>
                        <p class="text-gray-600">No associated sports found.</p>
                    <% } %>
                </div>
            </div>

            <!-- Associated Music -->
            <div class="border-t border-gray-200 p-8 section-box">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Music</h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                    <% List<Music> musics = (List<Music>) request.getAttribute("musics");
                       if (musics != null && !musics.isEmpty()) {
                           for (Music music : musics) { %>
                    <div class="bg-white rounded-lg shadow overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= music.getImage() != null ? music.getImage() : "/images/placeholder.jpg" %>" 
                             alt="<%= music.getArtistName() != null ? music.getArtistName() : "Music Image" %>" 
                             class="item-image" 
                             onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-4">
                            <h3 class="text-lg font-semibold text-[#002B5B] mb-2"><%= music.getArtistName() != null ? music.getArtistName() : "Unknown Artist" %></h3>
                            <p class="text-gray-600 text-sm mb-2 line-clamp-2"><%= music.getDescription() != null ? music.getDescription() : "No description available." %></p>
                            <p class="text-sm text-gray-500 mb-2"><i class="fas fa-music mr-2"></i><%= music.getGenre() != null ? music.getGenre() : "Unknown Genre" %></p>
                            <p class="text-sm text-gray-500 mb-2"><i class="fas fa-calendar-alt mr-2"></i><%= music.getFormationYear() > 0 ? music.getFormationYear() : "Not specified" %></p>
                            <div class="mt-4">
                                <a href="${pageContext.request.contextPath}/music-detail?id=<%= music.getId() %>" 
                                   class="text-[#F4A300] hover:text-[#A31621] text-sm">
                                    View Details →
                                </a>
                            </div>
                        </div>
                    </div>
                    <%      }
                       } else { %>
                        <p class="text-gray-600">No associated music found.</p>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300">
                        Discover the rich culture, cuisine, and beauty of Nepal through our comprehensive guide.
                    </p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="${pageContext.request.contextPath}/index" class="text-gray-300 hover:text-[#F4A300]">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="${pageContext.request.contextPath}/attractions" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="${pageContext.request.contextPath}/music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
                        <li><a href="${pageContext.request.contextPath}/movies" class="text-gray-300 hover:text-[#F4A300]">Movies</a></li>
                        <li><a href="${pageContext.request.contextPath}/sports" class="text-gray-300 hover:text-[#F4A300]">Sports</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Contact Us</h4>
                    <ul class="space-y-2 text-gray-300">
                        <li><i class="fas fa-envelope mr-2"></i> info@nepalnavigator.com</li>
                        <li><i class="fas fa-phone mr-2"></i> +977 1234567890</li>
                        <li><i class="fas fa-map-marker-alt mr-2"></i> Kathmandu, Nepal</li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Follow Us</h4>
                    <div class="flex space-x-4">
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-facebook-f"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-twitter"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-instagram"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-youtube"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
                <p>© 2025 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <!-- JavaScript for Image Switching -->
    <script>
        function changeMainImage(src) {
            const mainImage = document.getElementById('main-image');
            mainImage.src = src;
            const thumbnails = document.querySelectorAll('.thumbnail');
            thumbnails.forEach(thumb => thumb.classList.remove('thumbnail-active'));
            const clickedThumbnail = event.target;
            clickedThumbnail.classList.add('thumbnail-active');
        }
    </script>
</body>
</html>