<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, model.Music, model.Celebrity, java.util.Collections" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nepali Music - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { font-family: 'Poppins', sans-serif; }
        .music-image { width: 100%; height: 200px; border-radius: 8px; object-fit: cover; }
        .celebrity-image { width: 40px; height: 40px; object-fit: cover; border-radius: 50%; }
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

    <!-- Hero Section -->
    <div class="relative h-[300px] bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1514525253161-7a46d19cd819?ixlib=rb-4.0.3&auto=format&fit=crop&w=1950&q=80');">
        <div class="absolute inset-0 bg-black bg-opacity-50"></div>
        <div class="relative container mx-auto px-4 h-full flex items-center">
            <div class="text-white">
                <h1 class="text-4xl font-bold mb-4">Nepali Music</h1>
                <p class="text-xl">Explore the rich musical heritage of Nepal</p>
            </div>
        </div>
    </div>

    <!-- Search Section -->
    <div class="bg-white shadow-md py-6">
        <div class="container mx-auto px-4">
            <div class="max-w-3xl mx-auto">
                <form action="${pageContext.request.contextPath}/music" method="GET" class="flex flex-col md:flex-row gap-4">
                    <div class="flex-1">
                        <input type="text" name="search" 
                               value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>" 
                               placeholder="Search for artists or bands..." 
                               class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#F4A300] focus:border-transparent">
                    </div>
                    <div class="flex gap-4">
                        <select name="genre" class="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#F4A300] focus:border-transparent">
                            <option value="all" <%= "all".equals(request.getAttribute("selectedGenre")) || request.getAttribute("selectedGenre") == null ? "selected" : "" %>>All Genres</option>
                            <option value="folk" <%= "folk".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Folk</option>
                            <option value="pop" <%= "pop".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Pop</option>
                            <option value="rock" <%= "rock".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Rock</option>
                            <option value="classical" <%= "classical".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Classical</option>
                            <option value="lok" <%= "lok".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Lok</option>
                            <option value="modern" <%= "modern".equals(request.getAttribute("selectedGenre")) ? "selected" : "" %>>Modern</option>
                        </select>
                        <button type="submit" class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                            <i class="fas fa-search mr-2"></i>Search
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Music Grid -->
    <div class="container mx-auto px-4 py-12">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <%
                List<Music> musicList = (List<Music>) request.getAttribute("musicList");
                Map<Integer, List<Celebrity>> musicCelebritiesMap = (Map<Integer, List<Celebrity>>) request.getAttribute("musicCelebritiesMap");
                if (musicList != null && !musicList.isEmpty() && musicCelebritiesMap != null) {
                    for (Music music : musicList) {
                        List<Celebrity> celebrities = musicCelebritiesMap.get(music.getId());
            %>
            <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                <img src="${pageContext.request.contextPath}<%= music.getImage() != null ? music.getImage() : "/images/placeholder.jpg" %>" 
                     alt="<%= music.getArtistName() != null ? music.getArtistName() : "Music Image" %>" 
                     class="music-image" 
                     onerror="this.src='https://via.placeholder.com/500'">
                <div class="p-6">
                    <div class="flex justify-between items-start">
                        <div>
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2"><%= music.getArtistName() != null ? music.getArtistName() : "Unknown Artist" %></h3>
                            <p class="text-gray-600 mb-2"><%= music.getDescription() != null ? music.getDescription() : "No description available." %></p>
                            <% if (celebrities != null && !celebrities.isEmpty()) { %>
                                <div class="flex items-center space-x-2 mb-2">
                                    <% for (int i = 0; i < Math.min(3, celebrities.size()); i++) { 
                                        Celebrity celeb = celebrities.get(i); %>
                                        <img src="${pageContext.request.contextPath}<%= celeb.getImage() != null && !celeb.getImage().isEmpty() ? celeb.getImage() : "/images/placeholder.jpg" %>" 
                                             alt="<%= celeb.getName() != null ? celeb.getName() : "Celebrity" %>" 
                                             class="celebrity-image" 
                                             onerror="this.src='https://via.placeholder.com/40'">
                                        <span class="text-sm text-gray-600"><%= celeb.getName() != null ? celeb.getName() : "Unknown" %></span>
                                    <% } %>
                                    <% if (celebrities.size() > 3) { %>
                                        <span class="text-sm text-gray-500">+<%= celebrities.size() - 3 %> more</span>
                                    <% } %>
                                </div>
                            <% } else { %>
                                <p class="text-sm text-gray-600 mb-2">No artists listed.</p>
                            <% } %>
                            <p class="text-sm text-gray-500"><i class="fas fa-music mr-2"></i><%= music.getGenre() != null ? music.getGenre() : "Unknown Genre" %></p>
                        </div>
                        <span class="bg-[#F4A300] text-white px-3 py-1 rounded-full text-sm"><%= music.getFormationYear() > 0 ? music.getFormationYear() : "N/A" %></span>
                    </div>
                    <div class="mt-6">
                        <a href="${pageContext.request.contextPath}/music-detail?id=<%= music.getId() %>" 
                           class="bg-[#F4A300] text-white px-4 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                            View Details
                        </a>
                    </div>
                </div>
            </div>
            <%
                    }
                } else {
            %>
            <div class="col-span-3 text-center text-gray-500">No music found matching your criteria.</div>
            <%
                }
            %>
        </div>

        <!-- Upcoming Events Section -->
        <div class="mt-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Upcoming Music Events</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                    <div class="p-6">
                        <h3 class="text-xl font-semibold text-[#002B5B] mb-2">Nepal Folk Festival</h3>
                        <p class="text-gray-600 mb-2">Date: June 10, 2025</p>
                        <p class="text-gray-600 mb-4">Location: Kathmandu Durbar Square</p>
                        <a href="#" class="text-[#F4A300] hover:text-[#A31621]">Learn More →</a>
                    </div>
                </div>
                <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                    <div class="p-6">
                        <h3 class="text-xl font-semibold text-[#002B5B] mb-2">Pop Music Concert</h3>
                        <p class="text-gray-600 mb-2">Date: July 5, 2025</p>
                        <p class="text-gray-600 mb-4">Location: Patan Durbar Square</p>
                        <a href="#" class="text-[#F4A300] hover:text-[#A31621]">Learn More →</a>
                    </div>
                </div>
                <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                    <div class="p-6">
                        <h3 class="text-xl font-semibold text-[#002B5B] mb-2">Rock Band Night</h3>
                        <p class="text-gray-600 mb-2">Date: August 15, 2025</p>
                        <p class="text-gray-600 mb-4">Location: Bhaktapur Durbar Square</p>
                        <a href="#" class="text-[#F4A300] hover:text-[#A31621]">Learn More →</a>
                    </div>
                </div>
            </div>
        </div>
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