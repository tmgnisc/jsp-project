<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
        body {
            font-family: 'Poppins', sans-serif;
        }
        .youtube-player {
            width: 100%;
            height: 150px;
            border-radius: 8px;
        }
        .artist-thumbnail {
            width: 100%;
            height: 120px;
            object-fit: cover;
            border-radius: 8px;
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
                    <% String username = (String) session.getAttribute("username");
                       if (username != null) { %>
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
    <div class="relative h-[300px] bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1544735716-392fe2489ffa?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80');">
        <div class="absolute inset-0 bg-black bg-opacity-50"></div>
        <div class="relative container mx-auto px-4 h-full flex items-center">
            <div class="text-white">
                <h1 class="text-4xl font-bold mb-4">Nepali Music</h1>
                <p class="text-xl">Discover the rich musical heritage of Nepal</p>
            </div>
        </div>
    </div>

    <!-- Search Section -->
    <div class="bg-white shadow-md py-6">
        <div class="container mx-auto px-4">
            <div class="max-w-3xl mx-auto">
                <form action="music" method="get">
                    <div class="flex flex-col md:flex-row gap-4">
                        <div class="flex-1">
                            <input type="text" name="search" value="${searchQuery}" placeholder="Search for artists..." 
                                   class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#F4A300] focus:border-transparent">
                        </div>
                        <div class="flex gap-4">
                            <select name="genre" class="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#F4A300] focus:border-transparent">
                                <option value="all" ${selectedGenre == 'all' ? 'selected' : ''}>All Genres</option>
                                <option value="folk" ${selectedGenre == 'folk' ? 'selected' : ''}>Folk</option>
                                <option value="pop" ${selectedGenre == 'pop' ? 'selected' : ''}>Pop</option>
                                <option value="rock" ${selectedGenre == 'rock' ? 'selected' : ''}>Rock</option>
                                <option value="classical" ${selectedGenre == 'classical' ? 'selected' : ''}>Classical</option>
                            </select>
                            <button type="submit" class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                                <i class="fas fa-search mr-2"></i>Search
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Music Grid -->
    <div class="container mx-auto px-4 py-12">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <%@ page import="java.util.List, model.Music" %>
            <% List<Music> musicList = (List<Music>) request.getAttribute("musicList");
               if (musicList != null && !musicList.isEmpty()) {
                   for (Music music : musicList) {
                       String youtubeUrl = music.getYoutubeChannelUrl();
                       String videoId = "";
                       if (youtubeUrl != null && youtubeUrl.contains("watch?v=")) {
                           videoId = youtubeUrl.split("v=")[1].split("&")[0];
                       }
            %>
            <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                <div class="p-6">
                    <!-- YouTube Player -->
                    <div class="mb-4">
                        <% if (!videoId.isEmpty()) { %>
                            <iframe class="youtube-player" 
                                    src="https://www.youtube.com/embed/<%= videoId %>?rel=0" 
                                    frameborder="0" 
                                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                    allowfullscreen>
                            </iframe>
                        <% } else { %>
                            <img src="https://ui-avatars.com/api/?name=<%= music.getArtistName() %>&size=200" 
                                 alt="<%= music.getArtistName() %> Fallback" 
                                 class="youtube-player">
                        <% } %>
                    </div>

                    <div class="flex justify-between items-start">
                        <div>
                            <h3 class="text-xl font-semibold text-[#002B5B] mb-2">
                                <a href="music-detail?id=<%= music.getId() %>" class="hover:text-[#F4A300]"><%= music.getArtistName() %></a>
                            </h3>
                            <p class="text-gray-600 mb-2">Formed in <%= music.getFormationYear() %></p>
                            <p class="text-sm text-gray-500"><i class="fas fa-user mr-2"></i><%= music.getArtistName() %></p>
                        </div>
                        <span class="bg-[#F4A300] text-white px-3 py-1 rounded-full text-sm"><%= music.getGenre() %></span>
                    </div>
                    
                    <!-- Music Links -->
                    <div class="mt-4 space-y-2">
                        <a href="<%= youtubeUrl != null ? youtubeUrl : '#' %>" 
                           class="flex items-center text-[#F4A300] hover:text-[#A31621]">
                            <i class="fab fa-youtube mr-2"></i>
                            <span>Visit YouTube Channel</span>
                        </a>
                    </div>
                    
                    <!-- Comment Prompt -->
                    <div class="mt-6">
                        <a href="music-detail?id=<%= music.getId() %>" 
                           class="text-[#F4A300] hover:text-[#A31621]">
                            View Details and Comments
                        </a>
                    </div>
                </div>
            </div>
            <% }
               } else { %>
               <p class="text-center text-gray-600 col-span-full">No artists found.</p>
            <% } %>
        </div>

        <!-- Famous Artists Section -->
        <div class="mt-16">
            <h2 class="text-3xl font-bold text-[#002B5B] mb-8">Famous Nepali Artists</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <% if (musicList != null && !musicList.isEmpty()) {
                       int count = 0;
                       for (Music music : musicList) {
                           if (count >= 4) break;
                           String youtubeUrl = music.getYoutubeChannelUrl();
                           String videoId = "";
                           if (youtubeUrl != null && youtubeUrl.contains("watch?v=")) {
                               videoId = youtubeUrl.split("v=")[1].split("&")[0];
                           }
                %>
                <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                    <% if (!videoId.isEmpty()) { %>
                        <img src="https://img.youtube.com/vi/<%= videoId %>/hqdefault.jpg" 
                             alt="<%= music.getArtistName() %> Thumbnail" 
                             class="artist-thumbnail">
                    <% } else { %>
                        <img src="https://ui-avatars.com/api/?name=<%= music.getArtistName() %>&size=200" 
                             alt="<%= music.getArtistName() %> Fallback" 
                             class="artist-thumbnail">
                    <% } %>
                    <div class="p-4">
                        <h3 class="text-lg font-semibold text-[#002B5B]"><%= music.getArtistName() %></h3>
                        <p class="text-sm text-gray-600"><%= music.getGenre() %> Music</p>
                    </div>
                </div>
                <%      count++;
                       }
                   } else { %>
                <p class="text-center text-gray-600 col-span-full">No famous artists available.</p>
                <% } %>
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
                        <li><a href="index" class="text-gray-300 hover:text-[#F4A300]">Home</a></li>
                        <li><a href="foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="attractions" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
                        <li><a href="movies" class="text-gray-300 hover:text-[#F4A300]">Movies</a></li>
                        <li><a href="sports" class="text-gray-300 hover:text-[#F4A300]">Sports</a></li>
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
                <p>© 2024 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>
</body>
</html>