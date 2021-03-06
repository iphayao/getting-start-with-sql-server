﻿using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SqlServerEFSample
{
    class EFSampleContext : DbContext
    {
        public EFSampleContext(string connectionString)
        {
            Database.SetInitializer<EFSampleContext>(new DropCreateDatabaseAlways<EFSampleContext>());
            this.Database.Connection.ConnectionString = connectionString;
        }
        public DbSet<User> Users { get; set; }
        public DbSet<Task> Tasks { get; set; }
    }
}
