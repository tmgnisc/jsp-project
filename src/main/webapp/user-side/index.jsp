<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.FoodItem" %>
<%@ page import="model.Attraction" %>
<%@ page import="model.Music" %>
<%@ page import="model.Movie" %>
<%@ page import="model.Sport" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nepal Navigator - Discover Nepal</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        .truncate-2-lines {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="container mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <a href="index" class="text-2xl font-bold text-[#F4A300]">Nepal Navigator</a>
                <div class="hidden md:flex space-x-6">
                    <a href="index" class="hover:text-[#F4A300]">Home</a>
                    <a href="foods" class="hover:text-[#F4A300]">Foods</a>
                    <a href="attractions" class="hover:text-[#F4A300]">Attractions</a>
                    <a href="music" class="hover:text-[#F4A300]">Music</a>
                    <a href="movies" class="hover:text-[#F4A300]">Movies</a>
                    <a href="sports" class="hover:text-[#F4A300]">Sports</a>
                    <% 
                        String username = (String) session.getAttribute("username");
                        if (username != null) { 
                    %>
                        <span class="text-white">Welcome, <%= username %>!</span>
                        <a href="logout" class="hover:text-[#F4A300]">Logout</a>
                    <% } else { %>
                        <a href="login" class="hover:text-[#F4A300]">Login/Register</a>
                    <% } %>
                </div>
                <button class="md:hidden">
                    <i class="fas fa-bars text-2xl"></i>
                </button>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="relative h-[600px] bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1544735716-392fe2489ffa?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80');">
        <div class="absolute inset-0 bg-black bg-opacity-50"></div>
        <div class="relative container mx-auto px-4 h-full flex items-center">
            <div class="text-white">
                <h1 class="text-5xl font-bold mb-4">Discover Nepal</h1>
                <p class="text-xl mb-8">Explore the rich culture, delicious cuisine, and breathtaking landscapes of Nepal</p>
                <a href="#explore" class="bg-[#F4A300] text-white px-8 py-3 rounded-full hover:bg-[#A31621] transition duration-300">Explore Now</a>
            </div>
        </div>
    </div>

    <!-- Top Sections -->
    <div id="explore" class="container mx-auto px-4 py-16">
        <!-- Top Foods -->
        <section class="mb-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Top 3 Foods</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <%
                    List<FoodItem> topFoods = (List<FoodItem>) request.getAttribute("topFoods");
                    if (topFoods != null && !topFoods.isEmpty()) {
                        for (FoodItem food : topFoods) {
                %>
                <a href="${pageContext.request.contextPath}/food-detail?id=<%= food.getId() %>" class="block">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= food.getImage() != null ? food.getImage() : "/images/placeholder.jpg" %>" alt="<%= food.getName() %>" class="w-full h-48 object-cover" onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-6">
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2 text-left"><%= food.getName() %></h3>
                            <p class="text-gray-600 truncate-2-lines text-left"><%= food.getDescription() != null ? food.getDescription() : "No description available." %></p>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="col-span-3 text-center text-gray-500">No foods available.</div>
                <%
                    }
                %>
            </div>
        </section>

        <!-- Top Attractions -->
        <section class="mb-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Top 3 Tourist Attractions</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <%
                    List<Attraction> topAttractions = (List<Attraction>) request.getAttribute("topAttractions");
                    if (topAttractions != null && !topAttractions.isEmpty()) {
                        for (Attraction attraction : topAttractions) {
                %>
                <a href="${pageContext.request.contextPath}/attraction-detail?id=<%= attraction.getId() %>" class="block">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= attraction.getImage() != null ? attraction.getImage() : "/images/placeholder.jpg" %>" alt="<%= attraction.getName() %>" class="w-full h-48 object-cover" onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-6">
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2 text-left"><%= attraction.getName() %></h3>
                            <p class="text-gray-600 truncate-2-lines text-left"><%= attraction.getDescription() != null ? attraction.getDescription() : "No description available." %></p>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="col-span-3 text-center text-gray-500">No attractions available.</div>
                <%
                    }
                %>
            </div>
        </section>

        <!-- Top Music -->
        <section class="mb-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Top 3 Songs</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <%
                    List<Music> topMusic = (List<Music>) request.getAttribute("topMusic");
                    if (topMusic != null && !topMusic.isEmpty()) {
                        for (Music music : topMusic) {
                %>
                <a href="${pageContext.request.contextPath}/music-detail?id=<%= music.getId() %>" class="block">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= music.getImage() != null ? music.getImage() : "/images/placeholder.jpg" %>" alt="<%= music.getArtistName() %>" class="w-full h-48 object-cover" onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-6">
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2 text-left"><%= music.getArtistName() %></h3>
                            <p class="text-gray-600 truncate-2-lines text-left"><%= music.getDescription() != null ? music.getDescription() : "No description available." %></p>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="col-span-3 text-center text-gray-500">No songs available.</div>
                <%
                    }
                %>
            </div>
        </section>

        <!-- Top Movies -->
        <section class="mb-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Top 3 Movies</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <%
                    List<Movie> topMovies = (List<Movie>) request.getAttribute("topMovies");
                    if (topMovies != null && !topMovies.isEmpty()) {
                        for (Movie movie : topMovies) {
                %>
                <a href="${pageContext.request.contextPath}/movie-detail?id=<%= movie.getId() %>" class="block">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= movie.getImage() != null ? movie.getImage() : "/images/placeholder.jpg" %>" alt="<%= movie.getTitle() %>" class="w-full h-48 object-cover" onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-6">
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2 text-left"><%= movie.getTitle() %></h3>
                            <p class="text-gray-600 truncate-2-lines text-left"><%= movie.getDescription() != null ? movie.getDescription() : "No description available." %></p>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="col-span-3 text-center text-gray-500">No movies available.</div>
                <%
                    }
                %>
            </div>
        </section>

        <!-- Top Sports -->
        <section class="mb-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Top 3 Sports</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <%
                    List<Sport> topSports = (List<Sport>) request.getAttribute("topSports");
                    if (topSports != null && !topSports.isEmpty()) {
                        for (Sport sport : topSports) {
                %>
                <a href="${pageContext.request.contextPath}/sport-detail?id=<%= sport.getId() %>" class="block">
                    <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= sport.getImage() != null ? sport.getImage() : "/images/placeholder.jpg" %>" alt="<%= sport.getName() %>" class="w-full h-48 object-cover" onerror="this.src='https://via.placeholder.com/500'">
                        <div class="p-6">
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2 text-left"><%= sport.getName() %></h3>
                            <p class="text-gray-600 truncate-2-lines text-left"><%= sport.getDescription() != null ? sport.getDescription() : "No description available." %></p>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="col-span-3 text-center text-gray-500">No sports available.</div>
                <%
                    }
                %>
            </div>
        </section>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white py-12">
        <div class="container mx-auto px-4">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300">Discover the beauty and culture of Nepal through our comprehensive guide.</p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="index" class="text-gray-300 hover:text-[#F4A300]">Home</a></li>
                        <li><a href="foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="scenery" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
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
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-twitter"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-instagram"></i></a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]"><i class="fab fa-youtube"></i></a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
                <p>© 2025 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>
</body>
</html>